  const filtro = document.getElementById('filtro');
    const filas = document.querySelectorAll('#tabla-deudores tr');

    filtro.addEventListener('input', () => {
        const texto = filtro.value.toLowerCase();
        filas.forEach(fila => {
            const contenido = fila.innerText.toLowerCase();
            fila.style.display = contenido.includes(texto) ? '' : 'none';
        });
    });