// Función para abrir la ventana emergente
function openLayer(title, description, repo) {
    var layer = document.querySelector('.layer');
    var layerTitle = document.querySelector('.layer-title');
    var layerDescription = document.querySelector('.layer-description');
    var layerRepo = document.querySelector('.layer-repo a');
  
    layerTitle.textContent = title;
    layerDescription.textContent = description;
    layerRepo.href = repo;
  
    layer.classList.add('show');
  }
  
  // Función para cerrar la ventana emergente
  function closeLayer() {
    var layer = document.querySelector('.layer');
    layer.classList.remove('show');
  }
  
  // Función para abrir la ventana emergente inicial
  function openWelcomePopup() {
    var welcomePopup = document.querySelector('#welcomePopup');
    welcomePopup.style.display = 'block';
    document.body.classList.add('blurred'); // Agregar clase para el efecto de fondo nublado
  }
  
  // Función para cerrar la ventana emergente inicial
  function closeWelcomePopup() {
    var welcomePopup = document.querySelector('#welcomePopup');
    welcomePopup.style.display = 'none';
    document.body.classList.remove('blurred'); // Quitar clase para el efecto de fondo nublado

  }
  
  // Evento al cargar la página
  window.addEventListener('load', function() {
    openWelcomePopup();
  });
  
  // Evento para cerrar la ventana emergente inicial al hacer clic en el botón "Cerrar"
  var closeWelcomePopupButton = document.querySelector('#closeWelcomePopup');
  closeWelcomePopupButton.addEventListener('click', function() {
    closeWelcomePopup();
  });
  
  // Evento para abrir la ventana emergente al hacer clic en el botón "Ver más"
  var openLayerButtons = document.querySelectorAll('.open-layer');
  openLayerButtons.forEach(function(button) {
    button.addEventListener('click', function(event) {
      event.preventDefault();
      var title = this.getAttribute('data-title');
      var description = this.getAttribute('data-description');
      var repo = this.getAttribute('data-repo');
      openLayer(title, description, repo);
    });
  });
  
  // Evento para cerrar la ventana emergente al hacer clic en el botón "Volver"
  var backButton = document.querySelector('.back-button');
  backButton.addEventListener('click', function() {
    closeLayer();
  });
  
  