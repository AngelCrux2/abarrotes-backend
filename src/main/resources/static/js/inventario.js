document.addEventListener('DOMContentLoaded', () => {
  const tablaInventario = document.getElementById('tabla-inventario');
  const mensajeVacio = document.getElementById('mensaje-vacio');

  fetch('/api/productos/todos')
    .then(response => {
      if (!response.ok) throw new Error('Error cargando productos');
      return response.json();
    })
    .then(productos => {
      if (productos.length === 0) {
        mensajeVacio.classList.remove('hidden');
        return;
      }
      productos.forEach(p => {
        const fila = document.createElement('tr');

        fila.innerHTML = `
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">${p.codigo}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 font-semibold">${p.nombre}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">$${p.costo}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm ${p.cantidad <= (p.stockMinimo ?? 5) ? 'text-red-600 font-bold' : 'text-gray-700'}">${p.cantidad}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">${p.caducidad ?? '-'}</td>
        `;

        tablaInventario.appendChild(fila);
      });
    })
    .catch(error => {
      mensajeVacio.textContent = "Error al cargar productos.";
      mensajeVacio.classList.remove('hidden');
      console.error(error);
    });
});

