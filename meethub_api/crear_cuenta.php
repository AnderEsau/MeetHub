<?php
// crear_cuenta.php
// Registra una cuenta nueva de alumno, docente o externo.
// Método: POST (JSON)
// Recibe: tipo_usuario, identificador (matrícula o RFC), nombre_completo, correo_electronico,
//         telefono, psw, pregunta_secreta y respuesta_secreta.
// Responde: exito y mensaje.
header("Content-Type: application/json; charset=UTF-8");
require_once "config/conexion.php";

$jsoninput = file_get_contents("php://input");
$data = json_decode($jsoninput, true);

$tipo = isset($data['tipo_usuario']) ? trim($data['tipo_usuario']) : "";              // Alumno, Docente o Externo
$identificador = isset($data['identificador']) ? trim($data['identificador']) : "";  // matrícula (alumno / docente) o RFC (externo)
$nombre_completo = isset($data['nombre_completo']) ? trim($data['nombre_completo']) : "";
$correo = isset($data['correo_electronico']) ? trim($data['correo_electronico']) : "";
$telefono = isset($data['telefono']) ? trim($data['telefono']) : "";
$psw = isset($data['psw']) ? trim($data['psw']) : "";
$pregunta = isset($data['pregunta_secreta']) ? trim($data['pregunta_secreta']) : "";
$respuesta = isset($data['respuesta_secreta']) ? trim($data['respuesta_secreta']) : "";

if ($tipo === 'Usuario Externo') {
    $tipo = 'Externo';
}

if (empty($tipo) || empty($identificador) || empty($nombre_completo) || empty($correo) ||
    empty($telefono) || empty($psw) || empty($pregunta) || empty($respuesta)) {
    header("HTTP/1.1 400 Bad Request");
    echo json_encode([
        "exito" => false,
        "mensaje" => "Todos los campos son obligatorios"
    ]);
    exit;
}

// Revisamos que los datos tengan el formato correcto.
if (!in_array($tipo, ['Alumno', 'Docente', 'Externo'])) {
    echo json_encode(["exito" => false, "mensaje" => "Tipo de usuario no válido"]);
    exit;
}

if ($tipo === 'Externo') {
    $identificador = mb_strtoupper($identificador, 'UTF-8');
    if (!preg_match('/^[A-ZÑ&]{3,4}\d{6}[A-Z0-9]{3}$/u', $identificador)) {
        echo json_encode(["exito" => false, "mensaje" => "El RFC no es válido"]);
        exit;
    }
} else {
    if (!ctype_digit($identificador) || strlen($identificador) > 9) {
        echo json_encode(["exito" => false, "mensaje" => "La matrícula debe contener solo números"]);
        exit;
    }
}

if (!filter_var($correo, FILTER_VALIDATE_EMAIL)) {
    echo json_encode(["exito" => false, "mensaje" => "El correo electrónico no es válido"]);
    exit;
}

if (!preg_match('/^\d{10}$/', $telefono)) {
    echo json_encode(["exito" => false, "mensaje" => "El teléfono debe tener 10 dígitos"]);
    exit;
}

// Mínimo 8 caracteres, con letras y números
if (!preg_match('/^(?=.*[A-Za-z])(?=.*\d).{8,}$/', $psw)) {
    echo json_encode(["exito" => false, "mensaje" => "La contraseña debe tener mínimo 8 caracteres, con letras y números"]);
    exit;
}

// Separamos el nombre completo en nombre, apellido paterno y apellido materno.
// Las dos últimas palabras son los apellidos y lo demás es el nombre.
// Ejemplo: "Lary Baruc Nolasco Linarte" queda como nombre "Lary Baruc",
// paterno "Nolasco" y materno "Linarte". Si solo hay dos palabras, no hay apellido materno.
$partes = preg_split('/\s+/', $nombre_completo, -1, PREG_SPLIT_NO_EMPTY);

if (count($partes) < 2) {
    echo json_encode(["exito" => false, "mensaje" => "Escribe tu nombre completo (nombre y apellidos)"]);
    exit;
}

if (count($partes) == 2) {
    $nombre = $partes[0];
    $paterno = $partes[1];
    $materno = "";
} else {
    $materno = array_pop($partes);
    $paterno = array_pop($partes);
    $nombre = implode(" ", $partes);
}

if (mb_strlen($nombre) > 100 || mb_strlen($paterno) > 100 || mb_strlen($materno) > 100 ||
    mb_strlen($correo) > 100 || mb_strlen($pregunta) > 150 || mb_strlen($respuesta) > 150) {
    echo json_encode(["exito" => false, "mensaje" => "Alguno de los campos excede la longitud permitida"]);
    exit;
}

try {
    // Revisamos que el correo no esté registrado en ninguna de las tres tablas.
    $existe = 0;
    foreach (["tblalumnos", "tbltrabajadores", "tblexterno"] as $tabla) {
        $stmt = $pdo->prepare("SELECT COUNT(*) FROM " . $tabla . " WHERE correo_electronico = ?");
        $stmt->execute([$correo]);
        $existe += $stmt->fetchColumn();
    }

    // También revisamos que la matrícula o el RFC no estén registrados.
    if ($tipo === 'Externo') {
        $stmt = $pdo->prepare("SELECT COUNT(*) FROM tblexterno WHERE rfc = ?");
        $stmt->execute([$identificador]);
    } else {
        $stmt = $pdo->prepare("SELECT (SELECT COUNT(*) FROM tblalumnos WHERE id_alumno = ?) + (SELECT COUNT(*) FROM tbltrabajadores WHERE matricula = ?)");
        $stmt->execute([(int) $identificador, (int) $identificador]);
    }
    $existe += $stmt->fetchColumn();

    if ($existe > 0) {
        echo json_encode(["exito" => false, "mensaje" => "Ya existe una cuenta con ese correo o matrícula/RFC"]);
        exit;
    }

    if ($tipo === 'Alumno') {
        $stmt = $pdo->prepare("INSERT INTO tblalumnos (id_alumno, nombre, apellido_paterno, apellido_materno, correo_electronico, telefono, psw, pregunta_secreta, respuesta_secreta)
                               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        $stmt->execute([(int) $identificador, $nombre, $paterno, $materno, $correo, $telefono, $psw, $pregunta, $respuesta]);

    } elseif ($tipo === 'Docente') {
        $stmt = $pdo->prepare("INSERT INTO tbltrabajadores (matricula, nombre, apellido_paterno, apellido_materno, correo_electronico, telefono, psw, pregunta_secreta, respuesta_secreta)
                               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        $stmt->execute([(int) $identificador, $nombre, $paterno, $materno, $correo, $telefono, $psw, $pregunta, $respuesta]);

    } else {
        // El formulario no pide la institución, por eso se guarda vacía.
        $stmt = $pdo->prepare("INSERT INTO tblexterno (rfc, nombre, apellido_paterno, apellido_materno, correo_electronico, telefono, institucion, psw, pregunta_secreta, respuesta_secreta)
                               VALUES (?, ?, ?, ?, ?, ?, '', ?, ?, ?)");
        $stmt->execute([$identificador, $nombre, $paterno, $materno, $correo, $telefono, $psw, $pregunta, $respuesta]);
    }

    echo json_encode([
        "exito" => true,
        "mensaje" => "Cuenta creada correctamente. Ya puedes iniciar sesión"
    ]);

} catch (Exception $e) {
    header("HTTP/1.1 500 Internal Server Error");
    echo json_encode([
        "exito" => false,
        "mensaje" => "Error en el servidor: " . $e->getMessage()
    ]);
}
?>
