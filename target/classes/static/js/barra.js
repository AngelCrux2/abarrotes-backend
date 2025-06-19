document.addEventListener('DOMContentLoaded', () => {
    const sidebar = document.getElementById('sidebar');
    const mainContent = document.getElementById('mainContent');
    const sidebarToggle = document.getElementById('sidebarToggle');
    const overlay = document.getElementById('overlay');
    const closeSidebarBtn = document.getElementById('closeSidebar');

    // Verificar estado inicial en escritorio
    const sidebarOculto = localStorage.getItem('sidebarOculto') === 'true';
    if (sidebarOculto && window.innerWidth >= 768) {
        ocultarSidebarEscritorio();
    }

    sidebarToggle?.addEventListener('click', () => {
        if (window.innerWidth < 768) {
            mostrarSidebarMovil();
        } else {
            // Desktop: alternar visibilidad
            if (sidebar.classList.contains('hidden')) {
                mostrarSidebarEscritorio();
            } else {
                ocultarSidebarEscritorio();
            }
        }
    });

    function ocultarSidebarEscritorio() {
        sidebar.classList.add('hidden');
        mainContent?.classList.remove('flex-1');
        mainContent?.classList.add('w-full');
        localStorage.setItem('sidebarOculto', true);
    }

    function mostrarSidebarEscritorio() {
        sidebar.classList.remove('hidden');
        mainContent?.classList.remove('w-full');
        mainContent?.classList.add('flex-1');
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

    closeSidebarBtn?.addEventListener('click', () => {
        if (window.innerWidth < 768) {
            ocultarSidebarMovil();
        }
    });

    overlay?.addEventListener('click', () => {
        if (window.innerWidth < 768) {
            ocultarSidebarMovil();
        }
    });

    // Corrige comportamiento en cambio de tamaño de pantalla
    window.addEventListener('resize', () => {
        if (window.innerWidth >= 768) {
            sidebar.classList.remove('-translate-x-full');
            overlay?.classList.add('hidden');
        } else if (localStorage.getItem('sidebarOculto') === 'true') {
            sidebar.classList.add('-translate-x-full');
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

