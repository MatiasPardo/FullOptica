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

  
  var bugController, spiderController;
  var bugsEnabled = true;
  
  function setBugs(bogarak, pokok) {
    bugController = new BugController({
      'minDelay': 500,
      'maxDelay': 505,
      'imageSprite': "https://pranx.com/images/fly-sprite.png",
      'minBugs': bogarak,
      'maxBugs': bogarak,
      'mouseOver': 'flyoff'
    });
  
    spiderController = new SpiderController({
      'minDelay': 1500,
      'maxDelay': 1505,
      'imageSprite': "https://pranx.com/images/spider-sprite.png",
      'minBugs': pokok,
      'maxBugs': pokok,
      'mouseOver': 'die'
    });
  }
  
  function showWelcomePopup() {
    document.getElementById('welcomePopup').style.display = 'block';
  }
  
  function hideWelcomePopup() {
    document.getElementById('welcomePopup').style.display = 'none';
    setTimeout(function() {
      setBugs(5, 3);
    }, 1000); // Esperar 1 segundo antes de mostrar los insectos y las arañas
    
    // Mostrar u ocultar los insectos y las arañas según el estado actual de bugsEnabled
    if (bugsEnabled) {
      bugController.show();
      spiderController.show();
    } else {
      bugController.hide();
      spiderController.hide();
    }
  }
  
  function toggleBugs() {
    bugsEnabled = !bugsEnabled;
    
    // Cambiar el nombre y la funcionalidad del botón según el estado actual de bugsEnabled
    var toggleButton = document.getElementById('toggleButton');
    if (bugsEnabled) {
      toggleButton.textContent = 'Desactivar insectos y arañas';
    } else {
      toggleButton.textContent = 'Activar insectos y arañas';
    }
  }
  
  document.getElementById('closeWelcomePopup').addEventListener('click', hideWelcomePopup);
  window.addEventListener('load', showWelcomePopup);
  document.getElementById('toggleButton').addEventListener('click', toggleBugs);
  

  
  
  
  
  