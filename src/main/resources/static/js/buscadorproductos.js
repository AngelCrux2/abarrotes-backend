document.addEventListener("DOMContentLoaded", function () {
    const input = document.getElementById('buscador');
    const resultadosDiv = document.getElementById('resultados');
    const mensajeDiv = document.getElementById('mensaje');
    const botonCompra = document.getElementById('btn-comprar');

    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    let carrito = [];

   input.addEventListener('input', () => {
       const valor = input.value.trim();

       if (valor.length >= 2) {
           fetch(`/api/productos/buscar-parcial?valor=${encodeURIComponent(valor)}&limite=5`)
               .then(response => {
                   if (!response.ok) throw new Error("Error en la búsqueda");
                   return response.json();
               })
               .then(productos => {
                   resultadosDiv.innerHTML = '';

                   if (productos.length === 0) {
                       resultadosDiv.innerHTML = `
                           <p class="text-gray-600 text-center p-4 bg-yellow-100 rounded">No se encontraron productos.</p>`;
                       return;
                   }

                   productos.forEach(p => {
                       const card = document.createElement('div');
                       card.className = `flex flex-col md:flex-row justify-between items-start md:items-center
                                         bg-white border border-gray-300 rounded-lg p-4 mb-3 shadow`;

                       card.innerHTML = `
                           <div class="flex-1 space-y-1">
                               <h5 class="text-lg font-semibold text-gray-800">${p.nombre}</h5>
                               <p class="text-sm text-gray-600">
                                   <span class="hidden md:inline">Código: <span class="font-medium">${p.codigo}</span> | </span>
                                   Precio: <span class="text-green-700 font-semibold">$${p.costo}</span> |
                                   Stock: <span class="text-blue-700 font-semibold">${p.cantidad}</span>
                               </p>
                           </div>
                           <button class="mt-2 md:mt-0 md:ml-4 px-4 py-2 bg-blue-600 text-white font-semibold
                                          rounded hover:bg-blue-700 transition seleccionar-btn">
                               + Agregar
                           </button>
                       `;

                       card.querySelector('.seleccionar-btn').addEventListener('click', () => {
                           agregarAlCarrito(p);
                       });

                       resultadosDiv.appendChild(card);
                   });
               })
               .catch(err => {
                   resultadosDiv.innerHTML = `<p class="text-red-600 bg-red-100 p-3 rounded">${err.message}</p>`;
               });
       } else {
           resultadosDiv.innerHTML = "";
       }
   });


    function agregarAlCarrito(producto) {
        const existente = carrito.find(p => p.codigo === producto.codigo);
        if (existente) {
            existente.cantidadSeleccionada += 1;
        } else {
            carrito.push({
                nombre: producto.nombre,
                codigo: producto.codigo,
                costo: producto.costo,
                cantidadSeleccionada: 1
            });
        }

        input.value = "";
        resultadosDiv.innerHTML = "";
        mostrarCarrito();
    }

    function mostrarCarrito() {
        let html = `
            <h5 class="text-lg font-bold mb-2">Carrito:</h5>
            <div class="w-full overflow-x-auto">
                <table class="w-full border border-gray-300 text-sm mb-4">
                    <thead class="bg-gray-100 text-left">
                        <tr>
                            <th class="px-1 py-1 border whitespace-nowrap hidden md:table-cell">Código</th>
                            <th class="px-1 py-1 border whitespace-nowrap">Producto</th>
                            <th class="px-1 py-1 border whitespace-nowrap text-center">Cantidad</th>
                            <th class="px-1 py-1 border whitespace-nowrap">Subtotal</th>
                            <th class="px-1 py-1 border whitespace-nowrap text-center">Eliminar</th>
                        </tr>
                    </thead>
                    <tbody>
        `;

        let total = 0;

        if (carrito.length === 0) {
            html += `
                <tr>
                    <td colspan="5" class="text-center px-2 py-2 text-gray-500">Tu carrito está vacío.</td>
                </tr>
            `;
        } else {
            carrito.forEach((p, index) => {
                const subtotal = (p.costo || 0) * p.cantidadSeleccionada;
                total += subtotal;
                html += `
                    <tr>
                        <td class="px-1 py-1 border hidden md:table-cell">${p.codigo}</td>
                        <td class="px-1 py-1 border">${p.nombre}</td>
                        <td class="px-1 py-1 border text-center">
                            <input type="number" value="${p.cantidadSeleccionada}" min="1"
                                onchange="cambiarCantidadInput(${index}, this.value)"
                                class="w-14 text-sm text-center border rounded" />
                        </td>
                        <td class="px-1 py-1 border">$${subtotal.toFixed(2)}</td>
                        <td class="px-1 py-1 border text-center">
                            <button onclick="eliminarProducto(${index})"
                                class="text-lg hover:opacity-70" title="Eliminar">🗑️</button>
                        </td>
                    </tr>
                `;
            });
        }

        html += `
                    </tbody>
                </table>
            </div>
            <p class="text-right font-semibold text-sm">Total: $${total.toFixed(2)}</p>
        `;

        mensajeDiv.innerHTML = html;
    }


   function cambiarCantidadInput(index, nuevaCantidad) {
       const cantidad = parseInt(nuevaCantidad);
       if (!isNaN(cantidad) && cantidad > 0) {
           carrito[index].cantidadSeleccionada = cantidad;
           mostrarCarrito();
       }
   }

    function eliminarProducto(index) {
        carrito.splice(index, 1);
        mostrarCarrito();
    }
    window.cambiarCantidadInput = cambiarCantidadInput;
    window.eliminarProducto = eliminarProducto;

    botonCompra.addEventListener('click', () => {
        if (carrito.length === 0) {
            mensajeDiv.innerHTML = `<p style="color:red;">Agrega productos primero.</p>`;
            return;
        }

        const headers = {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        };

        fetch(`/api/productos/comprar`, {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(carrito)
        })
            .then(response => {
                if (!response.ok) throw new Error("Error al procesar la compra");
                return response.json();
            })
            .then(data => {
                mensajeDiv.innerHTML = `<p style="color:green;">Compra realizada con éxito.</p>`;
                carrito = [];
                mostrarCarrito();
                input.value = "";
                resultadosDiv.innerHTML = "";
            })
            .catch(err => {
                mensajeDiv.innerHTML = `<p style="color:red;">${err.message}</p>`;
            });
    });
    mostrarCarrito(); // Mostrar la tabla al cargar la página
});


