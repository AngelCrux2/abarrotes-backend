document.addEventListener('DOMContentLoaded', () => {
    const resultadosEditarDiv = document.getElementById('resultadosEditar');

    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    const modal = document.getElementById('modalConfirmacion');
    const btnCancelar = document.getElementById('btnCancelar');
    const btnConfirmarEliminar = document.getElementById('btnConfirmarEliminar');
    let idProductoAEliminar = null;

    const mostrarError = (mensaje) => {
        const divError = document.getElementById('mensajeError');
        const textoError = document.getElementById('mensajeErrorTexto');
        textoError.textContent = mensaje;
        divError.classList.remove('hidden');
        setTimeout(() => {
            divError.classList.add('hidden');
        }, 4000);
    };

    // Función para renderizar productos
    const renderizarProductos = (productos) => {
        resultadosEditarDiv.innerHTML = '';

        if (productos.length === 0) {
            resultadosEditarDiv.innerHTML = `
                <p class="text-center text-gray-600 bg-yellow-100 p-3 rounded">
                    No se encontraron productos.
                </p>`;
            return;
        }

        productos.forEach(p => {
            const card = document.createElement('div');
            card.className = 'border rounded-lg p-4 shadow bg-white space-y-2';
            card.setAttribute('data-id', p.id);

            card.innerHTML = `
                <h4 class="font-semibold text-lg text-gray-700">${p.nombre}</h4>
                <p class="text-sm text-gray-500">Código: ${p.codigo}</p>
                <p class="text-sm text-gray-500">Precio: $${p.costo}</p>
                <div class="flex space-x-2">
                    <button onclick="window.location.href='/editarproducto/${p.id}'"
                            class="bg-yellow-500 hover:bg-yellow-600 text-white text-sm font-medium px-3 py-1 rounded">
                        Editar
                    </button>

                    <button data-id="${p.id}"
                            class="btn-eliminar bg-red-500 hover:bg-red-600 text-white text-sm font-medium px-3 py-1 rounded">
                        Eliminar
                    </button>
                </div>
            `;

            resultadosEditarDiv.appendChild(card);
        });

        // Asociar eventos eliminar
        document.querySelectorAll('.btn-eliminar').forEach(btn => {
            btn.addEventListener('click', e => {
                idProductoAEliminar = e.target.getAttribute('data-id');
                modal.classList.remove('hidden');
            });
        });
    };

    // Cargar todos los productos al iniciar
    fetch('/api/productos/todos')
        .then(response => {
            if (!response.ok) throw new Error("Error cargando productos");
            return response.json();
        })
        .then(productos => {
            renderizarProductos(productos);
        })
        .catch(err => {
            mostrarError(err.message);
        });

    // Modal botones
    btnCancelar.addEventListener('click', () => {
        modal.classList.add('hidden');
        idProductoAEliminar = null;
    });

    btnConfirmarEliminar.addEventListener('click', () => {
        if (!idProductoAEliminar) return;

        fetch(`/api/productos/${idProductoAEliminar}`, {
            method: 'DELETE',
            headers: {
                [csrfHeader]: csrfToken
            }
        })
        .then(response => {
            if (!response.ok) throw new Error("No se pudo eliminar el producto.");

            const card = document.querySelector(`div[data-id="${idProductoAEliminar}"]`);
            if (card) card.remove();

            modal.classList.add('hidden');
            idProductoAEliminar = null;
        })
        .catch(err => {
            modal.classList.add('hidden');
            mostrarError(err.message);
        });
    });

});
