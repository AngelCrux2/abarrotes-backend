document.addEventListener("DOMContentLoaded", () => {
  const btnRegistrarGasto = document.getElementById('btnRegistrarGasto');
  const formGasto = document.getElementById('formGasto');

  function validarMontos() {
    const inputMontoCaja = document.getElementById('montoCaja');
    const inputMontoBoveda = document.getElementById('montoBoveda');

    if (!inputMontoCaja || !inputMontoBoveda || !btnRegistrarGasto) return;

    const montoCaja = parseFloat(inputMontoCaja.value);
    const montoBoveda = parseFloat(inputMontoBoveda.value);

    const montoCajaValido = !isNaN(montoCaja) && montoCaja > 0;
    const montoBovedaValido = !isNaN(montoBoveda) && montoBoveda > 0;

    btnRegistrarGasto.disabled = !(montoCajaValido || montoBovedaValido);
  }

  validarMontos();

  formGasto?.addEventListener('submit', (e) => {
    e.preventDefault();

    const proveedor = document.getElementById('proveedor')?.value.trim() || "";
    const descripcion = document.getElementById('descripcion')?.value.trim() || "";
    const montoCaja = parseFloat(document.getElementById('montoCaja')?.value);
    const montoBoveda = parseFloat(document.getElementById('montoBoveda')?.value);

    if (proveedor === "" || descripcion === "") {
      alert("Por favor, completa el nombre del proveedor y la descripción del gasto.");
      return;
    }

    const montoCajaValido = !isNaN(montoCaja) && montoCaja > 0;
    const montoBovedaValido = !isNaN(montoBoveda) && montoBoveda > 0;

    if (!montoCajaValido && !montoBovedaValido) {
      alert("Debes ingresar una cantidad válida en al menos uno de los campos: 'Monto tomado de Caja' o 'Monto tomado de Bóveda'.");
      return;
    }

    formGasto.submit();
  });

  document.getElementById('montoCaja')?.addEventListener('input', validarMontos);
  document.getElementById('montoBoveda')?.addEventListener('input', validarMontos);
});

  setTimeout(() => {
        const msg = document.getElementById('error-message');
        if (msg) {
            msg.style.opacity = '0';
            setTimeout(() => msg.remove(), 500);
        }
    }, 2000);
