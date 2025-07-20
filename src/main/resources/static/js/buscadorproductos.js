let carrito = [];

document.addEventListener("DOMContentLoaded", function () {
    const input = document.getElementById('buscador');
    const resultadosDiv = document.getElementById('resultados');
    const botonCompra = document.getElementById('btn-comprar');
    const botonDeuda = document.getElementById('btn-añadir-deuda');
    const botonTransferencia = document.getElementById('btn-transferencia');

    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content') || '';
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content') || '';

    let temporizadorCodigo;

    input.addEventListener('input', () => {
        const valor = input.value.trim();

        if (valor.length >= 2) {
            fetch(`/api/productos/buscar-parcial?valor=${encodeURIComponent(valor)}&limite=5`)
                .then(response => {
                    if (!response.ok) throw new Error("Error en la búsqueda");
                    return response.json();
                })
                .then(productos => mostrarResultados(productos))
                .catch(() => {});
        } else {
            resultadosDiv.innerHTML = "";
        }

        clearTimeout(temporizadorCodigo);
        if (valor.length >= 4) {
            temporizadorCodigo = setTimeout(() => {
                fetch(`/api/productos/buscar-codigo?codigo=${encodeURIComponent(valor)}`)
                    .then(response => {
                        if (!response.ok) return null;
                        return response.json();
                    })
                    .then(producto => {
                        if (producto) {
                            agregarAlCarrito(producto);
                            input.value = "";
                            resultadosDiv.innerHTML = "";
                        }
                    })
                    .catch(() => {});
            }, 500);
        }
    });

    function mostrarResultados(productos) {
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
    }

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
        const tbody = document.getElementById('tabla-carrito');
        const totalCarrito = document.getElementById('total-carrito');

        if (!tbody || !totalCarrito) {
            console.error("Elementos del carrito no encontrados");
            return;
        }

        let html = '';
        let total = 0;

        if (carrito.length === 0) {
            html = `
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

        tbody.innerHTML = html;
        totalCarrito.textContent = `Total: $${total.toFixed(2)}`;
    }

    function cambiarCantidadInput(index, nuevaCantidad) {
        const cantidad = parseInt(nuevaCantidad);
        if (!isNaN(cantidad) && cantidad > 0) {
            carrito[index].cantidadSeleccionada = cantidad;
            mostrarCarrito();
        } else {
            mostrarCarrito();
        }
    }

    function eliminarProducto(index) {
        carrito.splice(index, 1);
        mostrarCarrito();
    }

    function togglePanelVerduras() {
        const panel = document.getElementById("panel-verduras");
        panel.classList.toggle("hidden");
    }

    function agregarVerduraAVenta() {
        const nombre = document.getElementById("verdura").value;
        const costo = parseFloat(document.getElementById("costoVerdura").value);

        if (isNaN(costo) || costo <= 0) {
            return;
        }

        carrito.push({
            nombre: nombre,
            codigo: "VERDURA_" + nombre.toUpperCase() + "_" + Date.now(),
            costo: costo,
            cantidadSeleccionada: 1
        });

        mostrarCarrito();

        document.getElementById("costoVerdura").value = "";
        document.getElementById("panel-verduras").classList.add("hidden");
    }

   botonCompra.addEventListener('click', () => {
       if (carrito.length === 0) {
           return;
       }

       fetch('/api/productos/comprar', {
           method: 'POST',
           headers: {
               'Content-Type': 'application/json',
               [csrfHeader]: csrfToken
           },
           body: JSON.stringify(carrito)
       })
       .then(res => {
           if (!res.ok) throw new Error("Error al registrar la compra");
           return res.text();
       })
       .then(msg => {
           carrito = [];
           mostrarCarrito();

           const alerta = document.getElementById('alerta-venta');
           alerta.classList.remove('hidden');

           setTimeout(() => {
               alerta.classList.add('hidden');
           }, 2000);
       })
       .catch(err => {
           console.error("Error al registrar la compra:", err);
       });
   });


    botonDeuda.addEventListener('click', () => {
        if (carrito.length === 0) {
            alert("El carrito está vacío.");
            return;
        }

        fetch('/deudores/temp-carrito', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(carrito)
        })
        .then(res => {
            if (!res.ok) throw new Error("No se pudo guardar el carrito");
            window.location.href = '/deudores/seleccionar';
        })
        .catch(err => {
            alert("Error al enviar productos a deuda.");
        });
    });
    botonTransferencia.addEventListener('click', () => {
        if (carrito.length === 0) {
            alert("El carrito está vacío.");
            return;
        }

        fetch('/api/productos/comprar-transferencia', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(carrito)
        })
        .then(res => {
            if (!res.ok) throw new Error("Error al registrar la transferencia");
            return res.text();
        })
        .then(msg => {
            carrito = [];
            mostrarCarrito();
            const alerta = document.getElementById('alerta-transferencia');
               alerta.classList.remove('hidden');

               setTimeout(() => {
                   alerta.classList.add('hidden');
               }, 2000);
        })
        .catch(err => {
            mostrarAlertaEmergente("Ocurrió un error con la transferencia.", "error");
        });
    });

    window.cambiarCantidadInput = cambiarCantidadInput;
    window.eliminarProducto = eliminarProducto;
    window.togglePanelVerduras = togglePanelVerduras;
    window.agregarVerduraAVenta = agregarVerduraAVenta;
    window.mostrarCarrito = mostrarCarrito;

    mostrarCarrito();
});
