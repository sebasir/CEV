var SUMMARY_INDEX = 3; 

$(window).ready(function () {
    var form = $("#specimenForm").show();

    form.steps({
        headerTag: 'h3',
        bodyTag: 'section',
        transitionEffect: 'slideLeft',
        stepsOrientation: 'vertical',
        enableCancelButton: true,
        autoFocus: true,
        labels: {
            cancel: 'Cancelar',
            current: 'Paso Actual:',
            pagination: 'Paginación',
            finish: 'Finalizar',
            next: 'Siguiente',
            previous: 'Anterior',
            loading: 'Cargando ...'
        },
        onStepChanging: function (event, currentIndex, newIndex) {
            if (currentIndex > newIndex)
                return true;
            if (currentIndex < newIndex) {
                form.find(".body:eq(" + newIndex + ") label.error").remove();
                form.find(".body:eq(" + newIndex + ") .error").removeClass("error");
            }
            if (newIndex == SUMMARY_INDEX)
            	updateSummary();
           	return true;
        },
        onStepChanged: function (event, currentIndex, priorIndex) {
            $(".actions a:eq(1)").text("Siguiente");
        },
        onFinishing: function (event, currentIndex) {
            return true;
        },
        onFinished: function (event, currentIndex) {
            saveSpecimen();
        },
        onCanceled: function (event) {
            restart();
            window.location.reload();
        }
    });
});