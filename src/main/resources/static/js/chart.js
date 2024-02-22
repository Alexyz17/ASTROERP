    function openNav() {
        document.getElementById("mySidenav").style.width = "250px";
    }

    function closeNav() {
        document.getElementById("mySidenav").style.width = "0";
    }

function populateDataType() {
    var dataTypeSelect = document.getElementById('dataType');
}

populateDataType();

document.getElementById('dataType').addEventListener('change', function() {
    var selectedType = document.getElementById('dataType').value;
    var yearSelect = document.getElementById('year');
    var monthSelect = document.getElementById('month');
    var acceptButton = document.getElementById('acceptButton');
    var chartCanvas = document.getElementById('myChart');

    // Define los tipos de gráficos posibles
    var chartTypes = {
        'Usuarios registrados': 'bar',
        'Genero': 'doughnut',
        'Edad': 'doughnut',
        'Simpatizantes': 'line',
        'Eventos': 'bar',
        'SimpatizantesVsNo': 'doughnut',
        'Tipos de sugerencias': 'doughnut',
        'EventosActivos': 'bar',
        'SugerenciasDate': 'bar',
        'Balance': 'line',
        'Usuarios conectados': 'bar',
        'Cuotas': 'doughnut',
        'Roles': 'doughnut',
        'EventosUser': 'bar',
        'TipoEvento': 'doughnut',
        'notify': 'bar',
        'MaterialCount': 'bar',
        'Material': 'doughnut',
        'MaterialUbi': 'bar',
        'MembresiaCaducada': 'doughnut',
        'AuthMethod': 'doughnut',
        'UsuariosDNI': 'doughnut',
    };

    // Verifica si el tipo de gráfico seleccionado está definido
    if (selectedType in chartTypes) {
        var chartType = chartTypes[selectedType];

        // Actualiza el tipo de gráfico
        chart.config.type = chartType;
        chart.destroy(); // Destruye el gráfico existente
        chart = new Chart(ctx, chart.config); // Crea un nuevo gráfico con el tipo actualizado
    }

    // Oculta los desplegables de año y mes si se selecciona "Género de los usuarios"
    if (selectedType === 'Usuarios registrados' || selectedType === 'Simpatizantes' || selectedType === 'Eventos' || selectedType === 'SugerenciasDate' || selectedType === 'Balance' || selectedType === 'Usuarios conectados') {
        yearSelect.style.display = 'inline-block';
        monthSelect.style.display = 'inline-block';
        acceptButton.style.display = 'inline-block';
    } else {
        yearSelect.style.display = 'none';
        monthSelect.style.display = 'none';
        acceptButton.style.display = 'none';
    }


        var aspectRatio = 2;
        if (chartType === 'doughnut') {
            aspectRatio = 1;
        }


    // Dispara el evento click del botón "Aceptar" para cargar el gráfico
    acceptButton.click();
});
        var ctx = document.getElementById('myChart').getContext('2d');
        var chart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: [],
                datasets: [{
                    data: []
                }]
            },
            options: {
             responsive: true,
             aspectRatio: 1000,
                scales: {
                    xAxes: [{
                        type: 'time',
                        time: {
                            unit: 'month'
                        }
                    }],
                    yAxes: [{
                        ticks: {
                            beginAtZero: true
                        }
                    }]
                }
            }
        });

document.getElementById('year').addEventListener('change', function() {
    var year = document.getElementById('year').value;
    var monthSelect = document.getElementById('month');

    // Limpia las opciones existentes
    monthSelect.options.length = 0;

    if (year === "all") {
        monthSelect.options.add(new Option("Todos", "all"));
        monthSelect.style.display = 'inline-block';
    } else if (year && year !== "") {
        monthSelect.options.add(new Option("Todos", "all"));
        var months = ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"];
        for (var i = 0; i < months.length; i++) {
            monthSelect.options.add(new Option(months[i], i + 1));
        }
        monthSelect.style.display = 'inline-block';
    } else {
        monthSelect.style.display = 'none';
    }
});

document.getElementById('year').dispatchEvent(new Event('change'));

function populateYearAndMonth() {
    var yearSelect = document.getElementById('year');
    var monthSelect = document.getElementById('month');

    yearSelect.options.add(new Option("Todos", "all"));

    var currentYear = new Date().getFullYear();

    for (var year = currentYear; year >= 1900; year--) {
        yearSelect.options.add(new Option(year, year));
    }

    monthSelect.options.add(new Option("Todos", "all"));

    var months = [];
    for (var i = 0; i < months.length; i++) {
        monthSelect.options.add(new Option(months[i], i + 1));
    }
}

populateYearAndMonth();

function getDaysInMonth(month, year) {
    var date = new Date(year, month + 1, 0);
    var days = [];
    for (var i = 1; i <= date.getDate(); i++) {
        days.push(i);
    }
    return days;
}

document.getElementById('acceptButton').addEventListener('click', function() {
    var dataType = document.getElementById('dataType').value;
    var year = document.getElementById('year').value;
    var month = document.getElementById('month').value || "";


    fetch('/chartData?dataType=' + dataType + '&year=' + year + '&month=' + month)
        .then(response => response.json())
        .then(data => {
            var labels = Object.keys(data);
            var values = Object.values(data);

            var chartDataElement = document.getElementById('chartData');
            chartDataElement.innerHTML = JSON.stringify(data, null, 2);


            // Determina el tipo de gráfico basado en dataType
            var chartType = 'bar';
            var aspectRatio = 2;
            if (dataType === 'Usuarios registrados') {
                chartType = 'bar';
            } else if (dataType === 'Genero') {
                chartType = 'doughnut';
                aspectRatio = 2;
            } else if (dataType === 'Edad') {
                chartType = 'doughnut';
                aspectRatio = 2;
            } else if (dataType === 'Simpatizantes') {
                chartType = 'line';
                aspectRatio = 2;
            } else if (dataType === 'Eventos') {
                chartType = 'bar';
                aspectRatio = 2;
            } else if (dataType === 'SimpatizantesVsNo') {
                chartType = 'doughnut';
                aspectRatio = 2;
            } else if (dataType === 'Tipos de sugerencias') {
                chartType = 'doughnut';
                aspectRatio = 2;
            } else if (dataType === 'EventosActivos') {
                chartType = 'bar';
                aspectRatio = 2;
            } else if (dataType === 'SugerenciasDate') {
                chartType = 'bar';
                aspectRatio = 2;
            } else if (dataType === 'Balance') {
                chartType = 'line';
                aspectRatio = 2;
            } else if (dataType === 'Usuarios conectados') {
                chartType = 'bar';
                aspectRatio = 2;
            } else if (dataType === 'Cuotas') {
                chartType = 'doughnut';
                aspectRatio = 2;
            } else if (dataType === 'Roles') {
                chartType = 'doughnut';
                aspectRatio = 2;
            } else if (dataType === 'EventosUser') {
                chartType = 'bar';
                aspectRatio = 2;
            } else if (dataType === 'TipoEvento') {
                chartType = 'doughnut';
                aspectRatio = 2;
            } else if (dataType === 'notify') {
                chartType = 'bar';
                aspectRatio = 2;
            } else if (dataType === 'MaterialCount') {
                chartType = 'bar';
                aspectRatio = 2;
            } else if (dataType === 'Material') {
                chartType = 'doughnut';
                aspectRatio = 2;
            } else if (dataType === 'MaterialUbi') {
                chartType = 'bar';
                aspectRatio = 2;
            } else if (dataType === 'MembresiaCaducada') {
                chartType = 'doughnut';
                aspectRatio = 2;
            } else if (dataType === 'AuthMethod') {
                chartType = 'doughnut';
                aspectRatio = 2;
            } else if (dataType === 'UsuariosDNI') {
                chartType = 'doughnut';
                aspectRatio = 2;
            }

            // Actualiza el gráfico con los datos recibidos
            chart.destroy(); // Destruye el gráfico anterior
            chart = new Chart(ctx, {
                type: chartType,
                data: {
                    labels: labels,
                    datasets: [{
                        data: values
                    }]
                },
                options: {
                    responsive: true,
                    aspectRatio: aspectRatio,
                    scales: {
                        xAxes: [{
                            type: 'time',
                            time: {
                                unit: 'month'
                            }
                        }],
                        yAxes: [{
                            ticks: {
                                beginAtZero: true
                            }
                        }]
                    }
                }
            });
            chart.update();
        });
});

document.getElementById('downloadPdfButton').addEventListener('click', function() {
    var chartCanvas = document.getElementById('myChart');
    var chartImage = chartCanvas.toDataURL("image/jpeg", 1.0);

    // Obtén los datos del gráfico
    var chartDataElement = document.getElementById('chartData');
    var chartDataText = chartDataElement.innerHTML;

    // Crea un nuevo PDF
    var doc = new jsPDF('p', 'pt', 'a4');

    // Añade el gráfico al PDF
    doc.addImage(chartImage, 'JPEG', 40, 40, 500, 400);

    // Añade los datos del gráfico al PDF
    var splitText = doc.splitTextToSize(chartDataText, 500);
    doc.addPage();
    doc.text(splitText, 40, 40);

    doc.save('chart.pdf');
});

document.getElementById('sendEmailButton').addEventListener('click', function() {
    var chartDataElement = document.getElementById('chartData');
    var chartDataText = chartDataElement.innerHTML;

    // Solicita una dirección de correo electrónico
    var email = prompt("Direccion de correo:");

    fetch('/sendChart?email=' + encodeURIComponent(email) + '&data=' + encodeURIComponent(chartDataText))
    .then(response => {
        if (response.ok) {
            alert('Email enviado!!');
        } else {
            alert('Ups, ha habido un error!!');
        }
    });
});


// Logica de los eventos

var dataTypeSelect = document.getElementById('dataType');
var eventOption = document.createElement('option');
eventOption.value = 'Eventos';
eventOption.text = 'Eventos';
dataTypeSelect.add(eventOption);

dataTypeSelect.addEventListener('change', function() {
    var selectedType = dataTypeSelect.value;
    var eventSelect = document.getElementById('evento');

    if (selectedType === 'Eventos') {
        eventSelect.style.display = 'inline-block';

        fetch('/getEvents')
            .then(response => response.json())
            .then(data => {
                data.forEach(event => {
                    var option = document.createElement('option');
                    option.value = event.id;
                    option.text = event.name;
                    eventSelect.add(option);
                });
            });
    } else {
        eventSelect.style.display = 'none';
    }
});

document.getElementById('acceptButton').addEventListener('click', function() {
    var dataType = dataTypeSelect.value;
    var eventId = document.getElementById('evento').value;

    if (dataType === 'Eventos') {
        fetch('/chartData?dataType=' + dataType + '&eventId=' + eventId)
            .then(response => response.json())
            .then(data => {
                updateChart(data);
            });
    }
});
