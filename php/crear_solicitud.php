<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "config/conexion.php";

$jsoninput = file_get_contents("php://input");
$data = json_decode($jsoninput, true);

$tipo_usuario = isset($data['tipo_usuario']) ? trim($data['tipo_usuario']) : ""; // Alumno, Docente o Externo (el mismo que devolvió el login)
$id_usuario = isset($data['id_usuario']) ? intval($data['id_usuario']) : 0;
$id_sala = isset($data['id_sala']) ? intval($data['id_sala']) : 0;
$fecha_txt = isset($data['fecha_reserva']) ? trim($data['fecha_reserva']) : "";
$inicio_txt = isset($data['hora_inicio']) ? trim($data['hora_inicio']) : "";
$fin_txt = isset($data['hora_fin']) ? trim($data['hora_fin']) : "";
$asistentes = isset($data['total_asistentes']) ? intval($data['total_asistentes']) : 0;
$actividad = isset($data['descripcion_evento']) ? trim($data['descripcion_evento']) : ""; // "Actividad a realizar"

// Tabla y columna que le corresponden a cada tipo de usuario. La columna se llama igual en tblreserva.
$tipos = [
    "Alumno" => ["tblalumnos", "id_alumno"],
    "Docente" => ["tbltrabajadores", "matricula"],
    "Externo" => ["tblexterno", "id_externo"]
];

// La fecha puede llegar como 2026-11-20 o como 20/11/2026. Aquí la dejamos siempre como 2026-11-20.
$fecha = false;
foreach (['Y-m-d', 'd/m/Y'] as $formato) {
    $d = DateTime::createFromFormat($formato, $fecha_txt);
    if ($d && $d->format($formato) === $fecha_txt) {
        $fecha = $d->format('Y-m-d');
        break;
    }
}

// Las horas pueden llegar como 10:00 o como 10:00:00. Aquí las dejamos siempre como 10:00:00.
$hora_inicio = false;
$hora_fin = false;
if (preg_match('/^([01]?\d|2[0-3]):([0-5]\d)(:[0-5]\d)?$/', $inicio_txt, $m)) {
    $hora_inicio = sprintf('%02d:%s:00', $m[1], $m[2]);
}
if (preg_match('/^([01]?\d|2[0-3]):([0-5]\d)(:[0-5]\d)?$/', $fin_txt, $m)) {
    $hora_fin = sprintf('%02d:%s:00', $m[1], $m[2]);
}

if (!isset($tipos[$tipo_usuario]) || $id_usuario <= 0 || $id_sala <= 0 || !$fecha || !$hora_inicio || !$hora_fin ||
    $asistentes <= 0 || empty($actividad)) {
    header("HTTP/1.1 400 Bad Request");
    echo json_encode([
        "exito" => false,
        "mensaje" => "Completa todos los campos de la solicitud"
    ]);
    exit;
}

// Reglas de la solicitud: orden de las horas, día de la semana y días de anticipación.
if ($hora_inicio >= $hora_fin) {
    echo json_encode(["exito" => false, "mensaje" => "La hora de fin debe ser posterior a la hora de inicio"]);
    exit;
}

if ((int) date('N', strtotime($fecha)) > 5) {
    echo json_encode(["exito" => false, "mensaje" => "Solo se puede reservar de lunes a viernes"]);
    exit;
}

// La solicitud debe hacerse con al menos 3 días hábiles de anticipación.
// Los sábados y domingos no cuentan.
$minima = new DateTime('today');
$contados = 0;
while ($contados < 3) {
    $minima->modify('+1 day');
    if ((int) $minima->format('N') <= 5) {
        $contados++;
    }
}
if ($fecha < $minima->format('Y-m-d')) {
    echo json_encode(["exito" => false, "mensaje" => "La solicitud debe hacerse con al menos 3 días hábiles de anticipación (desde el " . $minima->format('d/m/Y') . ")"]);
    exit;
}

if (mb_strlen($actividad) > 255) {
    echo json_encode(["exito" => false, "mensaje" => "La actividad a realizar es demasiado larga"]);
    exit;
}

try {
    $tabla = $tipos[$tipo_usuario][0];
    $campo = $tipos[$tipo_usuario][1]; // id_alumno, matricula o id_externo (sale de la lista de arriba)

    // Revisamos que el usuario exista y esté activo.
    $stmt = $pdo->prepare("SELECT COUNT(*) FROM " . $tabla . " WHERE " . $campo . " = ? AND activo = 1");
    $stmt->execute([$id_usuario]);
    if ($stmt->fetchColumn() == 0) {
        echo json_encode(["exito" => false, "mensaje" => "El usuario no existe o está inactivo"]);
        exit;
    }

    // El horario debe ser una franja completa de tblhorarios, o varias franjas una tras otra.
    $stmt = $pdo->prepare("SELECT hora_inicio, hora_fin FROM tblhorarios
                           WHERE estado = 'Activo' AND hora_inicio >= ? AND hora_fin <= ?
                           ORDER BY hora_inicio ASC");
    $stmt->execute([$hora_inicio, $hora_fin]);
    $franjas = $stmt->fetchAll();

    $completo = count($franjas) > 0
        && $franjas[0]['hora_inicio'] === $hora_inicio
        && $franjas[count($franjas) - 1]['hora_fin'] === $hora_fin;
    for ($i = 1; $i < count($franjas); $i++) {
        if ($franjas[$i]['hora_inicio'] !== $franjas[$i - 1]['hora_fin']) {
            $completo = false;
        }
    }
    if (!$completo) {
        echo json_encode(["exito" => false, "mensaje" => "Elige uno o varios horarios completos de la lista de disponibilidad"]);
        exit;
    }

    $stmt = $pdo->prepare("SELECT capacidad, capacidad_minima, hora_apertura, hora_cierre FROM tblsalas WHERE id_sala = ? AND estado = 'Activa'");
    $stmt->execute([$id_sala]);
    $sala = $stmt->fetch();

    if (!$sala) {
        echo json_encode(["exito" => false, "mensaje" => "La sala no existe o no está disponible"]);
        exit;
    }

    if ($hora_inicio < $sala['hora_apertura'] || $hora_fin > $sala['hora_cierre']) {
        echo json_encode(["exito" => false, "mensaje" => "La sala atiende de " . substr($sala['hora_apertura'], 0, 5) . " a " . substr($sala['hora_cierre'], 0, 5) . " hrs"]);
        exit;
    }

    if ($asistentes < $sala['capacidad_minima'] || $asistentes > $sala['capacidad']) {
        echo json_encode(["exito" => false, "mensaje" => "El aforo de la sala es de " . $sala['capacidad_minima'] . " a " . $sala['capacidad'] . " personas"]);
        exit;
    }

    // No se puede pedir un horario que ya está aprobado ni uno que ya tiene otra solicitud pendiente.
    $stmt = $pdo->prepare("SELECT COUNT(*) FROM tblreserva
                           WHERE id_sala = ? AND fecha_reserva = ? AND hora_inicio < ? AND hora_fin > ?
                             AND estado_reserva IN ('Aprobada', 'Realizada', 'Pendiente')");
    $stmt->execute([$id_sala, $fecha, $hora_fin, $hora_inicio]);
    if ($stmt->fetchColumn() > 0) {
        echo json_encode(["exito" => false, "mensaje" => "Ese horario ya no está disponible para la sala seleccionada"]);
        exit;
    }

    // El formulario solo pide la actividad, así que también se usa como motivo.
    // El tipo de evento se guarda como "Académico".
    $stmt = $pdo->prepare("INSERT INTO tblreserva (id_sala, " . $campo . ", fecha_solicitud, fecha_reserva, hora_inicio, hora_fin,
                                                   tipo_evento, descripcion_evento, motivo_evento, total_asistentes, estado_reserva)
                           VALUES (?, ?, ?, ?, ?, ?, 'Académico', ?, ?, ?, 'Pendiente')");
    $stmt->execute([$id_sala, $id_usuario, date('Y-m-d'), $fecha, $hora_inicio, $hora_fin,
                    $actividad, mb_substr($actividad, 0, 150), $asistentes]);

    $id_reserva = (int) $pdo->lastInsertId();

    echo json_encode([
        "exito" => true,
        "mensaje" => "Solicitud enviada. Quedó pendiente de aprobación",
        "id_reserva" => $id_reserva,
        "folio" => sprintf('MH-%03d', $id_reserva)
    ]);

} catch (Exception $e) {
    header("HTTP/1.1 500 Internal Server Error");
    echo json_encode([
        "exito" => false,
        "mensaje" => "Error en el servidor: " . $e->getMessage()
    ]);
}
?>
