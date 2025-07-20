document.addEventListener('DOMContentLoaded', () => {
    const sidebar = document.getElementById('sidebar');
    const mainContent = document.getElementById('mainContent');
    const sidebarToggle = document.getElementById('sidebarToggle');
    const overlay = document.getElementById('overlay');
    const closeSidebarBtn = document.getElementById('closeSidebar');

    const isDesktop = window.innerWidth >= 768;
    const sidebarOculto = localStorage.getItem('sidebarOculto') === 'true';

    if (isDesktop) {
        if (sidebarOculto) {
            ocultarSidebarEscritorio();
        } else {
            mostrarSidebarEscritorio();
        }
    }

    sidebarToggle?.addEventListener('click', () => {
        if (window.innerWidth < 768) {
            mostrarSidebarMovil();
        } else {
            if (sidebar.classList.contains('-translate-x-full')) {
                mostrarSidebarEscritorio();
            } else {
                ocultarSidebarEscritorio();
            }
        }
    });

    function ocultarSidebarEscritorio() {
        sidebar.classList.add('-translate-x-full');
        mainContent.classList.remove('md:ml-72');
        localStorage.setItem('sidebarOculto', true);
    }

    function mostrarSidebarEscritorio() {
        sidebar.classList.remove('-translate-x-full');
        mainContent.classList.add('md:ml-72');
        localStorage.setItem('sidebarOculto', false);
    }

    function mostrarSidebarMovil() {
        sidebar.classList.remove('-translate-x-full');
        overlay?.classList.remove('hidden');
    }

    function ocultarSidebarMovil() {
        sidebar.classList.add('-translate-x-full');
        overlay?.classList.add('hidden');
    }

    closeSidebarBtn?.addEventListener('click', ocultarSidebarMovil);
    overlay?.addEventListener('click', ocultarSidebarMovil);

    window.addEventListener('resize', () => {
        const isNowDesktop = window.innerWidth >= 768;

        if (isNowDesktop) {
            overlay?.classList.add('hidden');
            if (localStorage.getItem('sidebarOculto') === 'true') {
                ocultarSidebarEscritorio();
            } else {
                mostrarSidebarEscritorio();
            }
        } else {
            ocultarSidebarEscritorio();
        }
    });
});
document.addEventListener('DOMContentLoaded', function () {
        const userMenuButton = document.getElementById('userMenuButton');
        const userDropdown = document.getElementById('userDropdown');

        userMenuButton.addEventListener('click', function (e) {
            e.stopPropagation();
            userDropdown.classList.toggle('hidden');
        });

        document.addEventListener('click', function (e) {
            if (!userMenuButton.contains(e.target)) {
                userDropdown.classList.add('hidden');
            }
        });
    });

document.addEventListener('DOMContentLoaded', () => {
  const campanaBtn = document.getElementById('campanaNotificaciones');
  const campanaAlerta = document.getElementById('campanaAlerta');
  const dropdown = document.getElementById('notificacionDropdown');
  const dropdownContenido = document.getElementById('notificacionContenido');
  const contador = document.getElementById('notiContador');

  if (!campanaBtn || !campanaAlerta || !dropdown || !dropdownContenido || !contador) return;

  campanaBtn.addEventListener('click', (e) => {
    e.stopPropagation();
    dropdown.classList.toggle('hidden');
  });

  document.addEventListener('click', (e) => {
    if (!campanaBtn.contains(e.target) && !dropdown.contains(e.target)) {
      dropdown.classList.add('hidden');
    }
  });

  fetch('/api/productos/todos')
    .then(res => {
      if (!res.ok) throw new Error('Error al cargar productos');
      return res.json();
    })
    .then(productos => {
      const proximosADias = 7;
      const fechaHoy = new Date();
      fechaHoy.setHours(0, 0, 0, 0);

      const criticos = [];
      const proximosAVencer = [];
      const caducados = [];

      productos.forEach(p => {
        if (p.cantidad <= (p.stockMinimo ?? 5)) criticos.push(p);

        if (!p.caducidad) return;

        const fechaCad = new Date(p.caducidad);
        fechaCad.setHours(0, 0, 0, 0);

        if (isNaN(fechaCad)) {
          console.warn(`Fecha inválida: ${p.caducidad}`);
          return;
        }

        const diffTime = fechaCad - fechaHoy;
        const diffDays = Math.round(diffTime / (1000 * 60 * 60 * 24));

        if (diffDays < 0) {
          caducados.push({ ...p, diffDays });
        } else if (diffDays <= proximosADias) {
          proximosAVencer.push({ ...p, diffDays });
        }
      });

      const totalNotificaciones = criticos.length + proximosAVencer.length + caducados.length;

      if (totalNotificaciones > 0) {
        campanaAlerta.classList.remove('hidden');
        contador.textContent = `${totalNotificaciones} nueva${totalNotificaciones > 1 ? 's' : ''}`;

        let html = '';

        criticos.forEach(p => {
          html += `
            <div class="px-4 py-3 flex items-start gap-3 hover:bg-gray-50 transition">
              <div class="mt-1">
                <svg class="w-5 h-5 text-red-500" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round"
                        d="M12 9v2m0 4h.01M4.93 4.93l14.14 14.14M12 3C7.03 3 3 7.03 3 12s4.03 9 9 9 9-4.03 9-9-4.03-9-9-9z"/>
                </svg>
              </div>
              <div class="text-sm text-gray-700">
                <p class="font-medium">${p.nombre}</p>
                <p class="text-xs text-gray-500">Stock actual: <span class="font-semibold">${p.cantidad}</span></p>
              </div>
            </div>
          `;
        });

        proximosAVencer.forEach(p => {
          let mensaje;
          if (p.diffDays === 0) mensaje = "Caduca hoy";
          else if (p.diffDays === 1) mensaje = "Caduca mañana";
          else mensaje = `Caduca en ${p.diffDays} días`;

          html += `
            <div class="px-4 py-3 flex items-start gap-3 hover:bg-yellow-50 transition">
              <div class="mt-1">
                <svg class="w-5 h-5 text-yellow-500" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round"
                        d="M12 8v4m0 4h.01M12 2a10 10 0 100 20 10 10 0 000-20z"/>
                </svg>
              </div>
              <div class="text-sm text-gray-700">
                <p class="font-medium">${p.nombre}</p>
                <p class="text-xs text-gray-500">${mensaje}</p>
              </div>
            </div>
          `;
        });

        caducados.forEach(p => {
          const dias = Math.abs(p.diffDays);
          const mensaje = dias === 1 ? `Caducó hace 1 día` : `Caducó hace ${dias} días`;

          html += `
            <div class="px-4 py-3 flex items-start gap-3 hover:bg-red-50 transition">
              <div class="mt-1">
                <svg class="w-5 h-5 text-red-600" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round"
                        d="M12 8v4m0 4h.01M12 2a10 10 0 100 20 10 10 0 000-20z"/>
                </svg>
              </div>
              <div class="text-sm text-gray-700">
                <p class="font-medium">${p.nombre}</p>
                <p class="text-xs text-red-600">${mensaje}</p>
              </div>
            </div>
          `;
        });

        dropdownContenido.innerHTML = html;

      } else {
        campanaAlerta.classList.add('hidden');
        contador.textContent = `0 nuevas`;
        dropdownContenido.innerHTML = `<div class="px-4 py-3 text-sm text-gray-700">No hay alertas actuales.</div>`;
      }
    })
    .catch(err => {
      dropdownContenido.innerHTML = `<div class="px-4 py-3 text-sm text-red-500">Error al cargar notificaciones.</div>`;
      console.error('Error:', err);
    });
});



