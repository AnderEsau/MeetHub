<?php
header("Content-Type: application/json; charset=UTF-8");

require_once "config/conexion.php";

try {

    $datos = json_decode(file_get_contents("php://input"), true);

    if (
        !isset($datos["identificador"]) ||
        !isset($datos["respuesta"]) ||
        !isset($datos["nueva_password"])
    ) {
        header("HTTP/1.1 400 Bad Request");
        echo json_encode(["error" => "Faltan datos obligatorios"]);
        exit;
    }

    $identificador = $datos["identificador"];
    $respuesta = $datos["respuesta"];
    $nueva_password = $datos["nueva_password"];
    $confirmar_password = isset($datos["confirmar_password"]) ? $datos["confirmar_password"] : "";

    if ($identificador === "" || $respuesta === "" || $nueva_password === "") {
        header("HTTP/1.1 400 Bad Request");
        echo json_encode(["error" => "Faltan datos para restablecer la contraseña"]);
        exit;
    }

    if ($confirmar_password !== "" && $confirmar_password !== $nueva_password) {
        header("HTTP/1.1 400 Bad Request");
        echo json_encode(["error" => "Las contraseñas no coinciden"]);
        exit;
    }

    $id_num = intval($identificador);
    $tipo = "";

    // 1. Buscar en alumnos
    $consulta = $pdo->prepare(
        "SELECT id_alumno AS id, respuesta_secreta
         FROM tblalumnos
         WHERE activo = 1 AND (correo_electronico = ? OR id_alumno = ?)"
    );
    $consulta->execute([$identificador, $id_num]);
    $usuario = $consulta->fetch(PDO::FETCH_ASSOC);
    if ($usuario) {
        $tipo = "Alumno";
    }

    // 2. Buscar en trabajadores
    if (!$usuario) {
        $consulta = $pdo->prepare(
            "SELECT matricula AS id, respuesta_secreta
             FROM tbltrabajadores
             WHERE activo = 1 AND (correo_electronico = ? OR matricula = ?)"
        );
        $consulta->execute([$identificador, $id_num]);
        $usuario = $consulta->fetch(PDO::FETCH_ASSOC);
        if ($usuario) {
            $tipo = "Docente";
        }
    }

    // 3. Buscar en externos
    if (!$usuario) {
        $consulta = $pdo->prepare(
            "SELECT id_externo AS id, respuesta_secreta
             FROM tblexterno
             WHERE activo = 1 AND (correo_electronico = ? OR rfc = ?)"
        );
        $consulta->execute([$identificador, $identificador]);
        $usuario = $consulta->fetch(PDO::FETCH_ASSOC);
        if ($usuario) {
            $tipo = "Externo";
        }
    }

    if (!$usuario) {
        header("HTTP/1.1 404 Not Found");
        echo json_encode(["error" => "El usuario no está registrado"]);
        exit;
    }

    if ($respuesta !== $usuario["respuesta_secreta"]) {
        header("HTTP/1.1 400 Bad Request");
        echo json_encode(["error" => "La respuesta secreta es incorrecta"]);
        exit;
    }

    if ($tipo === "Alumno") {
        $consulta = $pdo->prepare(
            "UPDATE tblalumnos
             SET psw = ?
             WHERE id_alumno = ?"
        );
    } elseif ($tipo === "Docente") {
        $consulta = $pdo->prepare(
            "UPDATE tbltrabajadores
             SET psw = ?
             WHERE matricula = ?"
        );
    } else {
        $consulta = $pdo->prepare(
            "UPDATE tblexterno
             SET psw = ?
             WHERE id_externo = ?"
        );
    }

    $consulta->execute([$nueva_password, $usuario["id"]]);

    header("HTTP/1.1 200 OK");
    echo json_encode([
        "mensaje" => "Contraseña actualizada correctamente"
    ]);

} catch (Exception $e) {
    header("HTTP/1.1 500 Internal Server Error");
    echo json_encode(["error" => "Ocurrió un error al restablecer la contraseña"]);
}
?>