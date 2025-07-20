function mostrarFormularioModificar() {
  document.getElementById('formModificar').classList.remove('hidden');
  document.getElementById('formRetirar').classList.add('hidden');
}

function mostrarFormularioRetirar() {
  document.getElementById('formRetirar').classList.remove('hidden');
  document.getElementById('formModificar').classList.add('hidden');
}

function changeTab(tabId) {
  document.querySelectorAll('.tab-content').forEach(tab => {
    tab.classList.add('hidden');
  });

  document.getElementById(`tab-${tabId}`).classList.remove('hidden');

  document.querySelectorAll('.tab-button').forEach(button => {
    button.classList.remove('active', 'border-blue-600', 'border-purple-600', 'border-amber-600', 'border-red-600');
    button.classList.add('border-transparent');
  });

  const activeButton = document.querySelector(`button[onclick="changeTab('${tabId}')"]`);
  activeButton.classList.add('active');

  if (tabId === 'caja') activeButton.classList.add('border-blue-600');
  if (tabId === 'proveedores') activeButton.classList.add('border-purple-600');
  if (tabId === 'ajustes') activeButton.classList.add('border-amber-600');
  if (tabId === 'retiros') activeButton.classList.add('border-red-600');
}

document.addEventListener('DOMContentLoaded', function() {
  changeTab('caja');
});

