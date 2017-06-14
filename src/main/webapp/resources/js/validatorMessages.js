$.extend($.validator.messages, {
	required : 'Este campo es obligatorio.',
	number : 'Ingresa un numero valido. (Entre 1 y 365)',
	email : 'Ingresa un correo valido.',
	max : $.validator.format("Por favor ingresa un valor menor a {0}."),
	min : $.validator.format("Por favor ingresa un valor mayor a {0}.")
});

var $valid = $("#obraForm").validate({
	errorPlacement : function errorPlacement(error, element) {
		element.after(error);
	},
	rules : {
		dias : {
			max : 365,
			min : 0
		},
		precio : {
			min : 0
		},
		identificacion : {
			min : 0
		},
		fechaVideo : {
			dateToday : true
		}
	}
});

$.validator.addMethod('dateToday', function(value, element) {
	var dateFormat = $(element).datepicker('option', 'dateFormat');
	try {
		var todayDate = $.datepicker.parseDate(dateFormat,
				$.datepicker.formatDate(dateFormat, new Date())).getTime();
		var enteredDate = $.datepicker.parseDate(dateFormat, value).getTime();
		return todayDate >= enteredDate;
	} catch (error) {
		return true;
	}
}, "Especifica una fecha menor o igual a hoy");

$.validator.addMethod('notExist', function(value, element) {
	var valid = false;
	$waiting.insertAfter(element);
	$.ajax({
		data : {
			newUser : value
		},
		type : "GET",
		url : urls.userServlet,
		success : function(data) {
			if (data.status == 'ok')
				valid = true;
			$waiting.remove();
		},
		error : function(data) {
			console.log(data);
		},
		async : false
	});
	return valid;
}, "El usuario ya existe");