<?php
require "conexion.php";
$cuentas = $conn->query("SELECT c.id_cuenta, c.numero_cuenta, c.tipo, cl.nombre
                         FROM cuentas c JOIN clientes cl ON cl.id_cliente = c.id_cliente
                         ORDER BY cl.nombre, c.numero_cuenta");
$opciones = "";
while ($c = $cuentas->fetch_assoc()) {
    $opciones .= "<option value='{$c['id_cuenta']}'>{$c['nombre']} - {$c['numero_cuenta']} ({$c['tipo']})</option>";
}
$msg  = $_GET['msg']  ?? "";
$tipo = $_GET['tipo'] ?? "";
?>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<title>Banco - Depositos y Retiros</title>
<link rel="stylesheet" href="estilos.css">
</head>
<body>
<h1>Sistema Bancario</h1>

<?php if ($msg != ""): ?>
  <p class="msg <?= $tipo == 'ok' ? 'ok' : 'error' ?>"><?= htmlspecialchars($msg) ?></p>
<?php endif; ?>

<h2>Realizar movimiento</h2>
<form action="procesar.php" method="POST" onsubmit="return validar()">
  <label>Cuenta:</label>
  <select name="id_cuenta" id="id_cuenta">
    <option value="">-- Seleccione --</option>
    <?= $opciones ?>
  </select>

  <label>Tipo:</label>
  <select name="tipo" id="tipo">
    <option value="DEPOSITO">Deposito</option>
    <option value="RETIRO">Retiro</option>
  </select>

  <label>Monto:</label>
  <input type="number" name="monto" id="monto" step="0.01" min="0.01">

  <button type="submit">Procesar</button>
</form>

<h2>Consultar historial</h2>
<form action="historial.php" method="GET" onsubmit="return validarConsulta()">
  <label>Cuenta:</label>
  <select name="id_cuenta" id="id_cuenta_consulta">
    <option value="">-- Seleccione --</option>
    <?= $opciones ?>
  </select>
  <button type="submit">Consultar</button>
</form>

<script src="script.js"></script>
</body>
</html>
