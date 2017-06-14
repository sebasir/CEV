$(window).ready(function () {
    var form = $("#specimenForm").show();
    console.log('HOLA');
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
            form.validate().settings.ignore = ":disabled,:hidden";
            return form.valid();
        },
        onStepChanged: function (event, currentIndex, priorIndex) {
            $(".actions a:eq(1)").text("Siguiente");

            if (priorIndex < currentIndex) {
                $('#progressbar').css('width', (priorIndex * 20 + 40) + '%');
            } else {
                $('#progressbar').css('width', (priorIndex * 20) + '%');
            }
        },
        onFinishing: function (event, currentIndex) {
            form.validate().settings.ignore = ":disabled";
            return form.valid();
        },
        onFinished: function (event, currentIndex) {
            restart();
            location.reload();
        },
        onCanceled: function (event) {
            restart();
            location.reload();
        }
    });
});