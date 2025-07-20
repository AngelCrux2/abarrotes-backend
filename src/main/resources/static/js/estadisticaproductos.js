  function colorDesdeTexto(texto) {
    let hash = 0;
    for (let i = 0; i < texto.length; i++) {
      hash = texto.charCodeAt(i) + ((hash << 5) - hash);
    }
    let color = "#";
    for (let i = 0; i < 3; i++) {
      const value = (hash >> (i * 8)) & 0xFF;
      color += ("00" + value.toString(16)).substr(-2);
    }
    return color;
  }

  fetch('/api/productos/reportes/todos-vendidos')
    .then(res => res.json())
    .then(data => {
      const semanas = [...new Set(data.map(d => d.numeroSemana))];
      const productos = [...new Set(data.map(d => d.nombreProducto))];

      const datasets = productos.map(nombre => {
        return {
          label: nombre,
          data: semanas.map(semana => {
            const encontrado = data.find(d => d.numeroSemana === semana && d.nombreProducto === nombre);
            return encontrado ? encontrado.totalVendido : 0;
          }),
          backgroundColor: colorDesdeTexto(nombre),
        };
      });

      const agrupado = {};
      data.forEach(d => {
        agrupado[d.nombreProducto] = (agrupado[d.nombreProducto] || 0) + d.totalVendido;
      });
      const productoMasVendido = Object.entries(agrupado).reduce((a, b) => b[1] > a[1] ? b : a);

      const ctx = document.getElementById('graficoProductos').getContext('2d');
      new Chart(ctx, {
        type: 'bar',
        data: {
          labels: semanas.map(s => 'Semana ' + s),
          datasets: datasets
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            title: { display: false },
            legend: { position: 'bottom' }
          },
          scales: {
            x: { stacked: false },
            y: { beginAtZero: true }
          }
        }
      });

      document.getElementById('destacado').innerText =
        `Producto más vendido: ${productoMasVendido[0]} con ${productoMasVendido[1]} unidades`;
    });
  fetch('/api/productos/reportes/ventas-por-departamento')
    .then(res => res.json())
    .then(data => {
      const nombres = data.map(d => d.nombreDepartamento);
      const totales = data.map(d => d.totalVendido);

      const ctxDep = document.getElementById('graficoDepartamentos').getContext('2d');
      new Chart(ctxDep, {
        type: 'pie',
        data: {
          labels: nombres,
          datasets: [{
            label: 'Ventas por Departamento',
            data: totales,
            backgroundColor: nombres.map(nombre => colorDesdeTexto(nombre)),
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            title: { display: false },
            legend: { position: 'right' }
          }
        }
      });
    });
  fetch('/api/productos/reportes/no-vendidos-7dias')
    .then(res => res.json())
    .then(data => {
      const lista = document.getElementById('productosNoVendidos');

      if (!data || data.length === 0) {
        lista.innerHTML = '<li>No hay productos sin venta en los últimos 7 días.</li>';
        return;
      }

      lista.innerHTML = data.map(p => `<li>${p.nombre}</li>`).join('');
    })
    .catch(error => {
      console.error('Error al cargar productos no vendidos:', error);
      const lista = document.getElementById('productosNoVendidos');
      lista.innerHTML = '<li>Error cargando datos.</li>';
    });
