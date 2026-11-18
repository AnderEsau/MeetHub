<?php
header("Content-Type: application/json; charset=UTF-8");

require_once "config/conexion.php";

try {

    $datos = json_decode(file_get_contents("php://input"), true);

    if (
        !isset($datos["tipo_usuario"]) ||
        !isset($datos["id_usuario"]) ||
        !isset($datos["id_reserva"]) ||
        !isset($datos["comentario"])
    ) {
        header("HTTP/1.1 400 Bad Request");
        echo json_encode(["error" => "Faltan datos obligatorios"]);
        exit;
    }

    $tipo_usuario = $datos["tipo_usuario"];
    $id_usuario = intval($datos["id_usuario"]);
    $id_reserva = intval($datos["id_reserva"]);
    $comentario = $datos["comentario"];

    if ($id_usuario <= 0 || $id_reserva <= 0 || $comentario === "") {
        header("HTTP/1.1 400 Bad Request");
        echo json_encode(["error" => "Escribe tu sugerencia o comentario"]);
        exit;
    }

    if ($tipo_usuario === "Alumno") {
        $consulta = $pdo->prepare(
            "SELECT id_reserva
             FROM tblreserva
             WHERE id_reserva = ? AND id_alumno = ? AND (estado_reserva = 'Realizada' OR estado_reserva = 'Aprobada')"
        );
    } elseif ($tipo_usuario === "Docente") {
        $consulta = $pdo->prepare(
            "SELECT id_reserva
             FROM tblreserva
             WHERE id_reserva = ? AND matricula = ? AND (estado_reserva = 'Realizada' OR estado_reserva = 'Aprobada')"
        );
    } elseif ($tipo_usuario === "Externo") {
        $consulta = $pdo->prepare(
            "SELECT id_reserva
             FROM tblreserva
             WHERE id_reserva = ? AND id_externo = ? AND (estado_reserva = 'Realizada' OR estado_reserva = 'Aprobada')"
        );
    } else {
        header("HTTP/1.1 400 Bad Request");
        echo json_encode(["error" => "Tipo de usuario no válido"]);
        exit;
    }

    $consulta->execute([$id_reserva, $id_usuario]);
    $reserva = $consulta->fetch(PDO::FETCH_ASSOC);

    if (!$reserva) {
        header("HTTP/1.1 404 Not Found");
        echo json_encode(["error" => "Solo puedes opinar sobre una sala que ya utilizaste"]);
        exit;
    }

    $consulta = $pdo->prepare(
        "SELECT COUNT(*) AS total
         FROM tblsugerencias
         WHERE id_reserva = ?"
    );
    $consulta->execute([$id_reserva]);
    $resultado = $consulta->fetch(PDO::FETCH_ASSOC);

    if (intval($resultado["total"]) > 0) {
        header("HTTP/1.1 409 Conflict");
        echo json_encode(["error" => "Ya enviaste una sugerencia para esta reserva"]);
        exit;
    }

    $consulta = $pdo->prepare(
        "INSERT INTO tblsugerencias
         (id_reserva, comentario)
         VALUES (?, ?)"
    );
    $consulta->execute([$id_reserva, $comentario]);

    $id_sugerencia = intval($pdo->lastInsertId());

    header("HTTP/1.1 201 Created");
    echo json_encode([
        "mensaje" => "Sugerencia enviada correctamente",
        "id_sugerencia" => $id_sugerencia,
        "id_reserva" => $id_reserva,
        "comentario" => $comentario
    ]);

} catch (Exception $e) {
    header("HTTP/1.1 500 Internal Server Error");
    echo json_encode(["error" => "Ocurrió un error al registrar la sugerencia"]);
}
?>