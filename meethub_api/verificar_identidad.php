<?php
header("Content-Type: application/json; charset=UTF-8");

require_once "config/conexion.php";

try {

    $datos = json_decode(file_get_contents("php://input"), true);

    if (!isset($datos["identificador"]) || $datos["identificador"] === "") {
        header("HTTP/1.1 400 Bad Request");
        echo json_encode(["error" => "Ingresa tu correo electrónico o matrícula"]);
        exit;
    }

    $identificador = $datos["identificador"];
    $respuesta = isset($datos["respuesta"]) ? $datos["respuesta"] : "";
    $id_num = intval($identificador);

    // 1. Buscar en alumnos
    $consulta = $pdo->prepare(
        "SELECT pregunta_secreta, respuesta_secreta
         FROM tblalumnos
         WHERE activo = 1 AND (correo_electronico = ? OR id_alumno = ?)"
    );
    $consulta->execute([$identificador, $id_num]);
    $usuario = $consulta->fetch(PDO::FETCH_ASSOC);

    // 2. Buscar en trabajadores
    if (!$usuario) {
        $consulta = $pdo->prepare(
            "SELECT pregunta_secreta, respuesta_secreta
             FROM tbltrabajadores
             WHERE activo = 1 AND (correo_electronico = ? OR matricula = ?)"
        );
        $consulta->execute([$identificador, $id_num]);
        $usuario = $consulta->fetch(PDO::FETCH_ASSOC);
    }

    // 3. Buscar en externos
    if (!$usuario) {
        $consulta = $pdo->prepare(
            "SELECT pregunta_secreta, respuesta_secreta
             FROM tblexterno
             WHERE activo = 1 AND (correo_electronico = ? OR rfc = ?)"
        );
        $consulta->execute([$identificador, $identificador]);
        $usuario = $consulta->fetch(PDO::FETCH_ASSOC);
    }

    if (!$usuario) {
        header("HTTP/1.1 404 Not Found");
        echo json_encode([
            "error" => "No existe una cuenta con ese correo o matrícula"
        ]);
        exit;
    }

    if ($respuesta === "") {
        header("HTTP/1.1 200 OK");
        echo json_encode([
            "mensaje" => "Pregunta encontrada",
            "pregunta" => $usuario["pregunta_secreta"]
        ]);
        exit;
    }

    if ($respuesta === $usuario["respuesta_secreta"]) {
        header("HTTP/1.1 200 OK");
        echo json_encode([
            "mensaje" => "Identidad validada. Ya puedes escribir tu nueva contraseña"
        ]);
    } else {
        header("HTTP/1.1 400 Bad Request");
        echo json_encode([
            "error" => "La respuesta secreta es incorrecta"
        ]);
    }

} catch (Exception $e) {
    header("HTTP/1.1 500 Internal Server Error");
    echo json_encode([
        "error" => "Ocurrió un error al verificar la identidad"
    ]);
}
?>