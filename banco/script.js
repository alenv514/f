function validar() {
  var cuenta = document.getElementById("id_cuenta").value;
  var monto = parseFloat(document.getElementById("monto").value);
  if (cuenta === "") {
    alert("Seleccione una cuenta");
    return false;
  }
  if (isNaN(monto) || monto <= 0) {
    alert("Ingrese un monto mayor a 0");
    return false;
  }
  return true;
}

function validarConsulta() {
  if (document.getElementById("id_cuenta_consulta").value === "") {
    alert("Seleccione una cuenta para consultar");
    return false;
  }
  return true;
}
