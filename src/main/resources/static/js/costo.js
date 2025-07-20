 document.addEventListener("DOMContentLoaded", function () {
        const costoBaseInput = document.getElementById('costoBase');
        const tipoCostoSelect = document.getElementById('tipoCosto');
        const costoCalculadoInput = document.getElementById('costo');

        function actualizarCosto() {
            const base = parseFloat(costoBaseInput.value) || 0;
            const tipo = tipoCostoSelect.value;
            let final = base;

            if (tipo.startsWith("ganancia")) {
                const porcentaje = parseInt(tipo.replace("ganancia", ""));
                final = base * (1 + porcentaje / 100);
            }

            costoCalculadoInput.value = final.toFixed(2);
        }

        costoBaseInput.addEventListener('input', actualizarCosto);
        tipoCostoSelect.addEventListener('change', actualizarCosto);
 });

 function buscarProductosSimilares(nombre) {
     const sugerenciasDiv = document.getElementById("sugerencias");
     sugerenciasDiv.innerHTML = "";

     if (nombre.length < 3) return;

     fetch(`/api/productos/similares?nombre=${encodeURIComponent(nombre)}`)
         .then(res => res.json())
         .then(productos => {
             if (productos.length === 0) return;

             sugerenciasDiv.innerHTML = `<p class="font-medium text-yellow-700">Productos similares encontrados:</p>`;
             productos.forEach(p => {
                 const elemento = document.createElement("div");
                 elemento.className = "flex justify-between items-center border px-3 py-2 rounded bg-yellow-50";

                 elemento.innerHTML = `
                     <div>
                         <strong>${p.nombre}</strong><br>
                         <span class="text-xs text-gray-500">Código: ${p.codigo}</span>
                     </div>
                     <a href="/productos/editarproducto/${p.id}" class="text-sm font-semibold text-yellow-600 hover:underline">Editar</a>
                 `;

                 sugerenciasDiv.appendChild(elemento);
             });
         })
         .catch(err => console.error("Error buscando productos similares:", err));
 }