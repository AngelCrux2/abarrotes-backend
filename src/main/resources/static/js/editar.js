document.addEventListener('DOMContentLoaded', () => {
  let idAEliminar = null;

  const buscador = document.getElementById('buscadorEditar');
  const contenedorResultados = document.getElementById('resultadosBusqueda');

  const modal = document.getElementById('modalEliminar');
  const btnCancelar = document.getElementById('cancelarEliminar');
  const btnConfirmar = document.getElementById('confirmarEliminar');

  const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

  let timeout = null;

  const mostrarError = (mensaje) => {
    alert('Error: ' + mensaje);
  };

  function asignarEventosEliminar() {
    document.querySelectorAll('.btn-eliminar').forEach(btn => {
      btn.removeEventListener('click', handleEliminarClick);
      btn.addEventListener('click', handleEliminarClick);
    });
  }

  function handleEliminarClick(event) {
    idAEliminar = event.currentTarget.getAttribute('data-id');
    modal.classList.remove('hidden');
  }

  function renderizarResultados(productos) {
    contenedorResultados.innerHTML = '';

    if (productos.length === 0) {
      contenedorResultados.innerHTML = `
        <p class="text-center col-span-full text-gray-500 bg-yellow-100 p-3 rounded">
          No se encontraron productos.
        </p>
      `;
      return;
    }

    productos.forEach(p => {
      const div = document.createElement('div');
      div.className = 'bg-white p-4 rounded-xl shadow border border-gray-200 space-y-2';
      div.setAttribute('data-id', p.id);
      div.innerHTML = `
        <h3 class="text-lg font-bold text-gray-800">${p.nombre}</h3>
        <p class="text-sm text-gray-600">
          Código: ${p.codigo}<br>
          Cantidad: ${p.cantidad}<br>
          Precio: $${p.costo}
        </p>
        <div class="flex gap-2 justify-end pt-2">
          <a href="/productos/editarproducto/${p.id}" class="px-4 py-2 bg-yellow-400 text-white rounded hover:bg-yellow-500">Editar</a>
          <button class="btn-eliminar px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600" data-id="${p.id}">Eliminar</button>
        </div>
      `;
      contenedorResultados.appendChild(div);
    });

    asignarEventosEliminar();
  }

  buscador?.addEventListener('input', () => {
    const valor = buscador.value.trim();
    clearTimeout(timeout);

    timeout = setTimeout(() => {
      if (valor === '') {
        contenedorResultados.innerHTML = '';
        return;
      }

      fetch(`/api/productos/buscar-parcial?valor=${encodeURIComponent(valor)}&limite=30`)
        .then(res => {
          if (!res.ok) throw new Error("Error al buscar productos");
          return res.json();
        })
        .then(renderizarResultados)
        .catch(err => mostrarError(err.message));
    }, 300);
  });

  btnConfirmar?.addEventListener('click', () => {
    if (!idAEliminar) return;

    fetch(`/api/productos/${idAEliminar}`, {
      method: 'DELETE',
            headers: {
              'Content-Type': 'application/json',
              [csrfHeader]: csrfToken
            }
    })
    .then(res => {
      if (!res.ok) throw new Error('No se pudo eliminar el producto');

      const card = document.querySelector(`[data-id="${idAEliminar}"]`)?.closest('.bg-white');
      if (card) card.remove();

      modal.classList.add('hidden');
      idAEliminar = null;
    })
    .catch(err => {
      modal.classList.add('hidden');
      mostrarError(err.message);
    });
  });

  btnCancelar?.addEventListener('click', () => {
    modal.classList.add('hidden');
    idAEliminar = null;
  });

  asignarEventosEliminar();
});
