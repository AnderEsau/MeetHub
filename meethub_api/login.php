<?php
// login.php
// Revisa el usuario y la contraseña de quien quiere entrar a la app.
// Método: POST (JSON)
// Recibe: identificador (correo, matrícula o RFC) y psw (la contraseña).
// Responde: exito, mensaje y, si los datos son correctos, los datos del usuario.
header("Content-Type: application/json; charset=UTF-8");
require_once "config/conexion.php";

// Leemos el cuerpo JSON que manda Retrofit desde Android.
$jsoninput = file_get_contents("php://input");
$data = json_decode($jsoninput, true);

$identificador = isset($data['identificador']) ? trim($data['identificador']) : ""; // correo, matrícula o RFC
$psw = isset($data['psw']) ? trim($data['psw']) : "";

if (empty($identificador) || empty($psw)) {
    header("HTTP/1.1 400 Bad Request");
    echo json_encode([
        "exito" => false,
        "mensaje" => "Debe ingresar el correo o matrícula y la contraseña"
    ]);
    exit;
}

try {
    // Buscamos al usuario en alumnos, trabajadores (docentes) y externos.
    // Cada elemento de la lista es: tipo, tabla, columna del id, columna donde se busca
    // (matrícula o RFC) y el valor que se busca.
    $num = ctype_digit($identificador) ? (int) $identificador : -1; // si no es un número usamos -1, para que "123abc" no se confunda con 123
    $tablas = [
        ["Alumno", "tblalumnos", "id_alumno", "id_alumno", $num],
        ["Docente", "tbltrabajadores", "matricula", "matricula", $num],
        ["Externo", "tblexterno", "id_externo", "rfc", $identificador]
    ];

    $usuario = null;
    foreach ($tablas as $t) {
        $stmt = $pdo->prepare("SELECT * FROM " . $t[1] . " WHERE activo = 1 AND (correo_electronico = ? OR " . $t[3] . " = ?)");
        $stmt->execute([$identificador, $t[4]]);
        $fila = $stmt->fetch();
        if ($fila) {
            $usuario = $fila;
            $tipo = $t[0];
            $id = (int) $fila[$t[2]];
            break;
        }
    }

    if ($usuario && $usuario['psw'] === $psw) {
        echo json_encode([
            "exito" => true,
            "mensaje" => "Bienvenido/a " . $usuario['nombre'],
            "tipo_usuario" => $tipo,                       // Alumno, Docente o Externo
            "id_usuario" => $id,                           // id_alumno, matrícula o id_externo
            "identificador" => ($tipo === "Externo") ? $usuario['rfc'] : (string) $id, // matrícula o RFC
            "nombre_completo" => trim($usuario['nombre'] . " " . $usuario['apellido_paterno'] . " " . $usuario['apellido_materno']),
            "correo_electronico" => $usuario['correo_electronico'],
            "telefono" => $usuario['telefono']
        ]);
    } else {
        echo json_encode([
            "exito" => false,
            "mensaje" => "Usuario o contraseña incorrectos"
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
