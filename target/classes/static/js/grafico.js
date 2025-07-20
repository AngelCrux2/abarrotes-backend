document.addEventListener("DOMContentLoaded", () => {
  renderChart("ventasPorDia", "Ventas diarias", true);
  renderChart("promedioSemanal", "Promedio semanal", false);
  const retiroInput = document.getElementById("montoRetiro");
  if (retiroInput) {
    retiroInput.addEventListener("input", recalcularCaja);
  }

  const formFinDia = document.getElementById('form-fin-dia');
  if (formFinDia) {
    formFinDia.addEventListener('submit', (e) => {
      if (!recalcularCaja()) {
        e.preventDefault();
        alert("El monto a retirar no puede ser mayor que el dinero en caja.");
      }
    });
  }

  adjustChartFonts();

  window.addEventListener('resize', () => {
    if (window.ventasChart) window.ventasChart.destroy();
    if (window.promedioChart) window.promedioChart.destroy();

    renderChart("ventasPorDia", "Ventas diarias", true);
    renderChart("promedioSemanal", "Promedio semanal", false);
    adjustChartFonts();
  });
});

function adjustChartFonts() {
  if (window.innerWidth < 640) {
    Chart.defaults.font.size = 10;
    Chart.defaults.plugins.tooltip.bodyFont.size = 11;
  } else if (window.innerWidth < 1024) {
    Chart.defaults.font.size = 11;
    Chart.defaults.plugins.tooltip.bodyFont.size = 12;
  } else {
    Chart.defaults.font.size = 12;
    Chart.defaults.plugins.tooltip.bodyFont.size = 13;
  }
}

function renderChart(id, label, mostrarPromedio) {
  const canvas = document.getElementById(id);
  if (!canvas) return;

  const ctx = canvas.getContext("2d");
  const labels = JSON.parse(canvas.dataset.labels || "[]");
  const data = JSON.parse(canvas.dataset.ventas || "[]");

  const datasets = [{
    label: label,
    data: data,
    backgroundColor: getBarColors(data.length),
    borderColor: "rgba(54, 162, 235, 1)",
    borderWidth: 1,
    borderRadius: 6,
    barPercentage: window.innerWidth < 640 ? 0.5 : 0.6
  }];

  if (mostrarPromedio && canvas.dataset.promedio) {
    const promedio = parseFloat(canvas.dataset.promedio);
    datasets.push({
      label: "Promedio",
      data: new Array(labels.length).fill(promedio),
      type: "line",
      borderColor: "rgba(255,99,132,0.8)",
      borderWidth: 2,
      fill: false,
      pointRadius: 0,
      tension: 0,
    });
  }

  const chart = new Chart(ctx, {
    type: "bar",
    data: {
      labels: labels,
      datasets: datasets
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        tooltip: {
          callbacks: {
            label: (context) => `$${context.parsed.y.toFixed(2)}`
          },
          padding: 10
        },
        legend: {
          position: 'top',
          labels: {
            boxWidth: 12
          }
        },
        datalabels: {
          display: window.innerWidth > 768,
          color: '#000',
          anchor: 'end',
          align: 'top',
          font: {
            weight: 'bold'
          },
          formatter: (value) => `$${value.toFixed(2)}`
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            callback: value => `$${value}`
          },
          grid: {
            color: 'rgba(0, 0, 0, 0.05)'
          }
        },
        x: {
          grid: {
            display: false
          }
        }
      }
    },
    plugins: [ChartDataLabels]
  });

  if(id === "ventasPorDia") window.ventasChart = chart;
  if(id === "promedioSemanal") window.promedioChart = chart;
}

function getBarColors(length) {
  const colores = [
    "rgba(54, 162, 235, 0.6)", "rgba(75, 192, 192, 0.6)",
    "rgba(153, 102, 255, 0.6)", "rgba(255, 159, 64, 0.6)",
    "rgba(100, 149, 237, 0.6)", "rgba(220, 20, 60, 0.6)"
  ];
  return Array.from({ length }, (_, i) => colores[i % colores.length]);
}

function recalcularCaja() {
  const ventas = parseFloat(document.getElementById("ventas")?.dataset.valor || 0);
  const gastos = parseFloat(document.getElementById("gastos")?.dataset.valor || 0);
  const saldoInicial = parseFloat(document.getElementById("saldoInicialEditado")?.value || 0);
  const retiroInput = document.getElementById("montoRetiro");
  const retiro = parseFloat(retiroInput?.value || 0);

  const caja = saldoInicial + ventas - gastos;
  const cajaRestante = caja - retiro;

  document.getElementById("dineroCaja").innerText = "$ " + cajaRestante.toFixed(2);
}

