<?php
require "conexion.php";
$id_cuenta = (int)($_GET['id_cuenta'] ?? 0);

$st = $conn->prepare("SELECT c.numero_cuenta, c.tipo, cl.nombre, cl.cedula
                      FROM cuentas c JOIN clientes cl ON cl.id_cliente = c.id_cliente
                      WHERE c.id_cuenta = ?");
$st->bind_param("i", $id_cuenta);
$st->execute();
$cuenta = $st->get_result()->fetch_assoc();
if (!$cuenta) {
    die("Cuenta no encontrada. <a href='index.php'>Volver</a>");
}

$st = $conn->prepare("SELECT id_movimiento, tipo, monto, fecha FROM movimientos
                      WHERE id_cuenta = ? ORDER BY fecha, id_movimiento");
$st->bind_param("i", $id_cuenta);
$st->execute();
$movs = $st->get_result();

$saldo = 0;
?>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<title>Historial de cuenta</title>
<link rel="stylesheet" href="estilos.css">
</head>
<body>
<h1>Historial de movimientos</h1>
<p><b>Cliente:</b> <?= htmlspecialchars($cuenta['nombre']) ?> (<?= $cuenta['cedula'] ?>)<br>
   <b>Cuenta:</b> <?= $cuenta['numero_cuenta'] ?> - <?= $cuenta['tipo'] ?></p>

<table>
  <tr><th>#</th><th>Fecha</th><th>Tipo</th><th>Monto</th></tr>
  <?php while ($m = $movs->fetch_assoc()):
      $saldo += ($m['tipo'] == 'DEPOSITO') ? $m['monto'] : -$m['monto']; ?>
  <tr>
    <td><?= $m['id_movimiento'] ?></td>
    <td><?= $m['fecha'] ?></td>
    <td class="<?= $m['tipo'] == 'DEPOSITO' ? 'dep' : 'ret' ?>"><?= $m['tipo'] ?></td>
    <td>$<?= number_format($m['monto'], 2) ?></td>
  </tr>
  <?php endwhile; ?>
</table>

<h2>Saldo total disponible: $<?= number_format($saldo, 2) ?></h2>
<a href="index.php">Volver</a>
</body>
</html>
