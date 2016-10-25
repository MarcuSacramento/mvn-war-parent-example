$(document).ready(function() {
	$('#nprocesso').mask('99999.999999/9999-99', {});
	$('#dataNasc').mask('99/99/9999', {});
	$('#dataEntrada').mask('99/99/9999', {});
	$('#dataEntrada').mask('99/99/9999', {});
	$('#dataSolicitacao').mask('99/99/9999', {});
	$('#caixa').attr("disabled", true);
	$('#check').click(function() {
		if ($('#check').is(":checked")) {
			$('#caixa').attr("disabled", false);
		} else {
			$('#caixa').attr("disabled", true);
		}
	});
	$('#nprocesso').blur(function() {
		var Hoje = new Date();
		var str = $('#nprocesso').val();
		var arr = str.split("/");
		var ano = arr[1].split("-");
	    var anoAtual = Hoje.getFullYear();
	    if(ano[0] <=  anoAtual){	
			$('#ano').val(ano[0]);
		}if(ano[0] < 1000){
			alert("É necessario que o ano seja maior o igual a 1000!!");
			$('#nprocesso').val("");
			$('#ano').val("");
	    }else if(ano[0] >  anoAtual){	 
			alert("É necessario que o ano seja menor o igual ao ano atual!!");
			$('#nprocesso').val("");
			$('#ano').val("");
		}

	});
      
	$('#ano').blur(function() {
		var ano =$('#ano').val();
		var Hoje = new Date();
	    var anoAtual = Hoje.getFullYear();
        if(ano < 1000){
			alert("É necessario que o ano seja maior o igual a 1000!!");
			$('#ano').val("");
	    }else if(ano > anoAtual){	 
			alert("É necessario que o ano seja menor o igual ao ano atual!!");
			$('#ano').val("");
		}

	});
	
	
	var maxLength = 50000;
	$('textarea').keyup(function() {
	  var length = $(this).val().length;
	  var length = maxLength-length;
	  $('#chars').text(length);
	});
      
	$(function() {

	    $('.sonums').keypress(function(event) {
	        var tecla = (window.event) ? event.keyCode : event.which;
	        if ((tecla > 47 && tecla < 58)) return true;
	        else {
	            if (tecla != 8) return false;
	            else return true;
	        }
	    });

	});
	
	/*
	$('#validate').submit(function(e) {
	    if($('#nprocesso').val() == "" ){
		  $('#nprocesso').css("border", "1px solid red");
		   return false;	
	   }if ($('#check').is(":checked")) {
		 if($('#caixa').val() == ""){
		    $('#caixa').css("border", "1px solid red");
		   return false;	
		 }
	   }if($('#NCompleto').val() == ""){
			 $('#NCompleto').css("border", "1px solid red");
		    return false;
		}else{
		    return true;
		}
	 });  */
	
});

