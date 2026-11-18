<?php
// obtener_mis_reservas.php
// Entrega las reservas de un usuario, de la más reciente a la más antigua.
// Método: GET
// Recibe: tipo_usuario, id_usuario y, de forma opcional, sin_sugerencia=1.
// Con sin_sugerencia=1 solo regresa las reservas que ya se usaron y todavía no tienen sugerencia.
// Eso sirve para llenar "Sala utilizada" en el Buzón.
// Responde: un arreglo JSON de reservas.
header("Content-Type: application/json; charset=UTF-8");
require_once "config/conexion.php";

$tipo_usuario = isset($_GET['tipo_usuario']) ? trim($_GET['tipo_usuario']) : "";
$id_usuario = isset($_GET['id_usuario']) ? intval($_GET['id_usuario']) : 0;
$sin_sugerencia = isset($_GET['sin_sugerencia']) && $_GET['sin_sugerencia'] == '1';

// Columna de tblreserva que le corresponde a cada tipo de usuario.
$campos = ["Alumno" => "id_alumno", "Docente" => "matricula", "Externo" => "id_externo"];

if (!isset($campos[$tipo_usuario]) || $id_usuario <= 0) {
    header("HTTP/1.1 400 Bad Request");
    echo json_encode(["error" => "Debe enviar un tipo de usuario y un id de usuario válidos"]);
    exit;
}

try {
    $sql = "SELECT r.id_reserva, r.id_sala, s.nombre_sala, r.fecha_reserva, r.hora_inicio, r.hora_fin,
                   r.total_asistentes, r.descripcion_evento, r.estado_reserva, r.motivo_rechazo_cancelacion
            FROM tblreserva r
            INNER JOIN tblsalas s ON s.id_sala = r.id_sala
            WHERE r." . $campos[$tipo_usuario] . " = ?";
    $params = [$id_usuario];

    if ($sin_sugerencia) {
        // Una reserva ya se usó si está Realizada, o si está Aprobada y su hora de fin ya pasó.
        // Además no debe tener una sugerencia.
        $sql .= " AND (r.estado_reserva = 'Realizada'
                       OR (r.estado_reserva = 'Aprobada' AND TIMESTAMP(r.fecha_reserva, r.hora_fin) <= ?))
                  AND NOT EXISTS (SELECT 1 FROM tblsugerencias g WHERE g.id_reserva = r.id_reserva)";
        $params[] = date('Y-m-d H:i:s');
    }

    $sql .= " ORDER BY r.fecha_reserva DESC, r.hora_inicio DESC";

    $stmt = $pdo->prepare($sql);
    $stmt->execute($params);
    $reservas = $stmt->fetchAll();

    // Convertimos los números a enteros para que coincidan con la data class de Kotlin.
    foreach ($reservas as &$r) {
        $r['folio'] = sprintf('MH-%03d', $r['id_reserva']);
        $r['id_reserva'] = (int) $r['id_reserva'];
        $r['id_sala'] = (int) $r['id_sala'];
        $r['total_asistentes'] = (int) $r['total_asistentes'];
        $r['hora_inicio'] = substr($r['hora_inicio'], 0, 5);
        $r['hora_fin'] = substr($r['hora_fin'], 0, 5);
    }

    echo json_encode($reservas, JSON_UNESCAPED_UNICODE);

} catch (Exception $e) {
    header("HTTP/1.1 500 Internal Server Error");
    echo json_encode(["error" => "Error al obtener las reservas: " . $e->getMessage()]);
}
?>
