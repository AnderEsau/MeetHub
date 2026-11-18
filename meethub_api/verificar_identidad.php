<?php
// verificar_identidad.php
// Primer paso para restablecer la contraseña. Atiende dos botones de la pantalla:
//   - Sin 'respuesta' ("Consultar pregunta"): regresa la pregunta secreta del usuario.
//   - Con 'respuesta' ("Validar respuesta"): dice si la respuesta secreta es correcta.
// Método: POST (JSON)
// Recibe: identificador (correo o matrícula) y, de forma opcional, respuesta.
// Responde: exito, mensaje y, al consultar la pregunta, la pregunta.
header("Content-Type: application/json; charset=UTF-8");
require_once "config/conexion.php";

$jsoninput = file_get_contents("php://input");
$data = json_decode($jsoninput, true);

$identificador = isset($data['identificador']) ? trim($data['identificador']) : ""; // correo o matrícula
$respuesta = isset($data['respuesta']) ? trim($data['respuesta']) : "";             // opcional

if (empty($identificador)) {
    header("HTTP/1.1 400 Bad Request");
    echo json_encode([
        "exito" => false,
        "mensaje" => "Ingresa tu correo electrónico o matrícula"
    ]);
    exit;
}

try {
    // Buscamos al usuario en alumnos, trabajadores (docentes) y externos.
    $num = ctype_digit($identificador) ? (int) $identificador : -1;
    $tablas = [
        ["tblalumnos", "id_alumno"],
        ["tbltrabajadores", "matricula"],
        ["tblexterno", "rfc"]
    ];

    $usuario = null;
    foreach ($tablas as $t) {
        $valor = ($t[0] === "tblexterno") ? $identificador : $num;
        $stmt = $pdo->prepare("SELECT pregunta_secreta, respuesta_secreta FROM " . $t[0] . " WHERE activo = 1 AND (correo_electronico = ? OR " . $t[1] . " = ?)");
        $stmt->execute([$identificador, $valor]);
        $usuario = $stmt->fetch();
        if ($usuario) {
            break;
        }
    }

    if (!$usuario) {
        echo json_encode([
            "exito" => false,
            "mensaje" => "No existe una cuenta con ese correo o matrícula"
        ]);
        exit;
    }

    // Si no llegó la respuesta, solo mostramos la pregunta secreta.
    if (empty($respuesta)) {
        echo json_encode([
            "exito" => true,
            "mensaje" => "Pregunta encontrada",
            "pregunta" => $usuario['pregunta_secreta']
        ]);
        exit;
    }

    // Si llegó la respuesta, la comparamos con la guardada sin importar mayúsculas ni espacios de sobra.
    if (mb_strtolower($respuesta, 'UTF-8') === mb_strtolower(trim($usuario['respuesta_secreta']), 'UTF-8')) {
        echo json_encode([
            "exito" => true,
            "mensaje" => "Identidad validada. Ya puedes escribir tu nueva contraseña"
        ]);
    } else {
        echo json_encode([
            "exito" => false,
            "mensaje" => "La respuesta secreta es incorrecta"
        ]);
    }

} catch (Exception $e) {
    header("HTTP/1.1 500 Internal Server Error");
    echo json_encode([
        "exito" => false,
        "mensaje" => "Error en el servidor: " . $e->getMessage()
    ]);
}
?>
