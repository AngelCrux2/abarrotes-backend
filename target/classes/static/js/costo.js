 document.addEventListener("DOMContentLoaded", function () {
        const costoBaseInput = document.getElementById('costoBase');
        const tipoCostoSelect = document.getElementById('tipoCosto');
        const costoCalculadoInput = document.getElementById('costo');

        function actualizarCosto() {
            const base = parseFloat(costoBaseInput.value) || 0;
            const tipo = tipoCostoSelect.value;
            let final = base;

            switch (tipo) {
                case 'neto20':
                    final = base * 1.2;
                    break;
                case 'neto40':
                    final = base * 1.4;
                    break;
                case 'final':
                default:
                    final = base;
            }

            costoCalculadoInput.value = final.toFixed(2);
        }

        // Actualiza el costo automáticamente cuando el usuario escribe o cambia selección
        costoBaseInput.addEventListener('input', actualizarCosto);
        tipoCostoSelect.addEventListener('change', actualizarCosto);
 });