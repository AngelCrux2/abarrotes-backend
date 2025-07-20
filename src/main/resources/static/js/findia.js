document.addEventListener("DOMContentLoaded", () => {
  function obtenerCajaCalculada() {
    const ventas = parseFloat(document.getElementById("ventas")?.dataset.valor || 0);
    const gastos = parseFloat(document.getElementById("gastos")?.dataset.valor || 0);
    const saldoInput = document.getElementById("saldoInicialEditado");

    let saldoInicial = parseFloat(saldoInput?.value);
    if (isNaN(saldoInicial) || saldoInicial === 0) {
      saldoInicial = parseFloat(saldoInput?.dataset.valor || 0);
    }

    return saldoInicial + ventas - gastos;
  }

  function mostrarCajaRestante() {
    const caja = obtenerCajaCalculada();
    const retiroInput = document.getElementById("montoRetiro");
    let retiro = parseFloat(retiroInput?.value);

    if (isNaN(retiro)) retiro = 0;

    const cajaRestante = caja - retiro;
    document.getElementById("dineroCaja").innerText = "$ " + cajaRestante.toFixed(2);
  }

  function validarRetiro() {
    const caja = obtenerCajaCalculada();
    const retiroInput = document.getElementById("montoRetiro");
    const retiro = parseFloat(retiroInput?.value || 0);
    const errorDiv = document.getElementById("errorRetiro");

    if (retiro > caja) {
      errorDiv.textContent = "❌ El monto a retirar no puede ser mayor que el dinero en caja.";
      errorDiv.classList.remove("hidden");
      retiroInput.classList.add("border-red-500");
      return false;
    } else {
      errorDiv.textContent = "";
      errorDiv.classList.add("hidden");
      retiroInput.classList.remove("border-red-500");
      return true;
    }
  }

  function recalcularCaja() {
    mostrarCajaRestante();
    validarRetiro();
  }

  document.getElementById("saldoInicialEditado")?.addEventListener("input", recalcularCaja);
  document.getElementById("montoRetiro")?.addEventListener("input", recalcularCaja);

  const caja = obtenerCajaCalculada();
  document.getElementById("dineroCaja").innerText = "$ " + caja.toFixed(2);
  validarRetiro();

  const formFinDia = document.getElementById('form-fin-dia');
  if (formFinDia) {
    formFinDia.addEventListener('submit', (e) => {
      if (!validarRetiro()) {
        e.preventDefault();
        alert("El monto a retirar no puede ser mayor que el dinero en caja.");
      }
    });
  }
});

