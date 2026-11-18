<?php
// restablecer_password.php
// Guarda la contraseña nueva (pantalla Restablecer contraseña, paso 2).
// Método: POST (JSON)
// Recibe: identificador, respuesta (la respuesta secreta), nueva_password y,
//         de forma opcional, confirmar_password.
// Responde: exito y mensaje.
header("Content-Type: application/json; charset=UTF-8");
require_once "config/conexion.php";

$jsoninput = file_get_contents("php://input");
$data = json_decode($jsoninput, true);

$identificador = isset($data['identificador']) ? trim($data['identificador']) : "";
$respuesta = isset($data['respuesta']) ? trim($data['respuesta']) : ""; // se vuelve a revisar aquí por seguridad
$nueva_password = isset($data['nueva_password']) ? trim($data['nueva_password']) : "";
$confirmar_password = isset($data['confirmar_password']) ? trim($data['confirmar_password']) : ""; // opcional

if (empty($identificador) || empty($respuesta) || empty($nueva_password)) {
    header("HTTP/1.1 400 Bad Request");
    echo json_encode([
        "exito" => false,
        "mensaje" => "Faltan datos para restablecer la contraseña"
    ]);
    exit;
}

// Mínimo 8 caracteres, con letras y números
if (!preg_match('/^(?=.*[A-Za-z])(?=.*\d).{8,}$/', $nueva_password)) {
    echo json_encode([
        "exito" => false,
        "mensaje" => "La contraseña debe tener mínimo 8 caracteres, con letras y números"
    ]);
    exit;
}

if ($confirmar_password !== "" && $confirmar_password !== $nueva_password) {
    echo json_encode([
        "exito" => false,
        "mensaje" => "Las contraseñas no coinciden"
    ]);
    exit;
}

try {
    // Buscamos al usuario en alumnos, trabajadores (docentes) y externos.
    // Cada elemento de la lista es: tabla, columna del id, columna donde se busca
    // (matrícula o RFC) y el valor que se busca.
    $num = ctype_digit($identificador) ? (int) $identificador : -1;
    $tablas = [
        ["tblalumnos", "id_alumno", "id_alumno", $num],
        ["tbltrabajadores", "matricula", "matricula", $num],
        ["tblexterno", "id_externo", "rfc", $identificador]
    ];

    $usuario = null;
    foreach ($tablas as $t) {
        $stmt = $pdo->prepare("SELECT " . $t[1] . " AS id, respuesta_secreta FROM " . $t[0] . " WHERE activo = 1 AND (correo_electronico = ? OR " . $t[2] . " = ?)");
        $stmt->execute([$identificador, $t[3]]);
        $fila = $stmt->fetch();
        if ($fila) {
            $usuario = $fila;
            $tabla = $t[0];
            $campo_id = $t[1];
            break;
        }
    }

    if (!$usuario || mb_strtolower($respuesta, 'UTF-8') !== mb_strtolower(trim($usuario['respuesta_secreta']), 'UTF-8')) {
        echo json_encode([
            "exito" => false,
            "mensaje" => "La respuesta secreta es incorrecta"
        ]);
        exit;
    }

    // La tabla y la columna salen de la lista de arriba, nunca de lo que escribe el usuario.
    $stmt = $pdo->prepare("UPDATE " . $tabla . " SET psw = ? WHERE " . $campo_id . " = ?");
    $stmt->execute([$nueva_password, $usuario['id']]);

    echo json_encode([
        "exito" => true,
        "mensaje" => "Contraseña actualizada correctamente"
    ]);

} catch (Exception $e) {
    header("HTTP/1.1 500 Internal Server Error");
    echo json_encode([
        "exito" => false,
        "mensaje" => "Error en el servidor: " . $e->getMessage()
    ]);
}
?>
