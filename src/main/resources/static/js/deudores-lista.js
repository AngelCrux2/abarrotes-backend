 document.addEventListener("DOMContentLoaded", function () {
    const buscador = document.getElementById("buscadorDeudores");

    buscador.addEventListener("input", function () {
      const filtro = buscador.value.toLowerCase();

      document.querySelectorAll("table tbody tr").forEach(fila => {
        const celdas = fila.querySelectorAll("td");
        if (celdas.length >= 2) {
          const nombre = celdas[0].textContent.toLowerCase();
          const telefono = celdas[1].textContent.toLowerCase();
          const coincide = nombre.includes(filtro) || telefono.includes(filtro);
          fila.style.display = coincide ? "" : "none";
        }
      });

      document.querySelectorAll(".md\\:hidden > div").forEach(card => {
        const textos = card.querySelectorAll("span, p");
        let nombre = "", telefono = "";
        textos.forEach(el => {
          const texto = el.textContent.toLowerCase();
          if (texto.match(/[a-z]/i) && !nombre) nombre = texto;
          else if (texto.match(/[0-9]{7,}/) && !telefono) telefono = texto;
        });

        const coincide = nombre.includes(filtro) || telefono.includes(filtro);
        card.style.display = coincide ? "" : "none";
      });
    });
  });
