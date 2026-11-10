<?php
require "conexion.php";

function volver($msg, $tipo) {
    header("Location: index.php?tipo=$tipo&msg=" . urlencode($msg));
    exit;
}

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    volver("Solicitud invalida", "error");
}

$id_cuenta = (int)($_POST['id_cuenta'] ?? 0);
$tipo      = $_POST['tipo'] ?? "";
$monto     = (float)($_POST['monto'] ?? 0);

if ($id_cuenta <= 0 || !in_array($tipo, ['DEPOSITO', 'RETIRO']) || $monto <= 0) {
    volver("Datos invalidos", "error");
}

$conn->begin_transaction();

// Bloquea la cuenta mientras se valida el saldo
$st = $conn->prepare("SELECT id_cuenta FROM cuentas WHERE id_cuenta = ? FOR UPDATE");
$st->bind_param("i", $id_cuenta);
$st->execute();
if ($st->get_result()->num_rows == 0) {
    $conn->rollback();
    volver("La cuenta no existe", "error");
}

if ($tipo == 'RETIRO') {
    $st = $conn->prepare("SELECT COALESCE(SUM(CASE WHEN tipo='DEPOSITO' THEN monto ELSE -monto END),0) AS saldo
                          FROM movimientos WHERE id_cuenta = ?");
    $st->bind_param("i", $id_cuenta);
    $st->execute();
    $saldo = (float)$st->get_result()->fetch_assoc()['saldo'];
    if ($monto > $saldo) {
        $conn->rollback();
        volver("Retiro rechazado: el monto supera el saldo disponible ($" . number_format($saldo, 2) . ")", "error");
    }
}

$st = $conn->prepare("INSERT INTO movimientos (id_cuenta, tipo, monto) VALUES (?, ?, ?)");
$st->bind_param("isd", $id_cuenta, $tipo, $monto);
$st->execute();
$conn->commit();

volver(($tipo == 'DEPOSITO' ? "Deposito" : "Retiro") . " realizado correctamente", "ok");
