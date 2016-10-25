// Definições globais
var strSeletorModal = 'div.siouv_modal_form';
var strSeletorSombra = 'div.siouv_sombra';

function atualizaCharLeft(txtSeletor){
	/* Atualiza quantidade de caracteres disponíveis em textarea */
	txtCharLeft = 'span' + txtSeletor + '_left';
	txtSeletor = 'textarea' + txtSeletor;
	intTamanhoMax = $(txtSeletor).attr('maxlength');
	intCharLeft = intTamanhoMax - $(txtSeletor).val().length;
	if(intCharLeft < 0) {
		$(txtCharLeft).text(0);
		$(txtSeletor).text($(txtSeletor).text().substring(0,intTamanhoMax));
	} else {
		$(txtCharLeft).text(intCharLeft);
	}
}

function inicializaCharLeft(txtSeletor){
	/* Inicializa eventos de atualização de quantidade de caracteres disponíveis em textarea */
	atualizaCharLeft(txtSeletor);
	$(txtSeletor).keyup(function(){ atualizaCharLeft(txtSeletor); });
	$(txtSeletor).blur(function(){ atualizaCharLeft(txtSeletor); });
}

function configuraTextArea(){
	/* Adiciona atributo maxlength em textarea e prepara exibição de caracteres disponíveis no campo */
	$('textarea[class^="maxlength"]').each(function(){
		intMaxLength = ($(this).attr('class')).replace('maxlength_','');
		strId = $(this).attr('id');
		if ($('#'+strId+'_left').length == 0) {
			if(strId==undefined){
				strId='txt_'+(Math.floor(Math.random()*10000)+1);
				$(this).attr('id',strId);
			}
			txtHtml = '<br /><label class="siouv_left"><em>Caracteres restantes: </em><span id="'+strId+'_left" class="numero_caracter" >'+intMaxLength+'</span></label>';	
			$(this).attr('maxlength',intMaxLength);
			strFind = "#"+strId+"_left";
			if($(strFind).length==0){
				$(this).after(txtHtml);
			}
			inicializaCharLeft('#'+strId);
		}
	});
}

function reposicionaModais(){
	$(strSeletorModal).each(function(){
		var obj = $(this);
		var posX = -obj.width()/2;
		var posY = -obj.height()/2;
		obj.css('margin-top',posY+'px').css('margin-left',posX+'px');
	});
}
function callFunctionAjaxRequest(data) {  
    if (data.status == "success")  
    	reposicionaModais();  
}  
function showDiv(id){
	/* Centralizar e exibe div */
	var strSeletor='#'+id;
/*	var posX=-$(strSeletor).width()/2;
	var posY=-$(strSeletor).height()/2;
	$(strSeletor).css('margin-top',posY+'px').css('margin-left',posX+'px'); */
	reposicionaModais();
	$(strSeletor).fadeIn(200);
}

function showModal(id){
	/* Mostra modal estilo formulário */
	$('div.siouv_sombra').fadeIn(200);
	showDiv(id);
}

var idAlerta;

function showAlertaAjax(id){
	idAlerta=id;
	
}

function hideAlertaAjax(id){
	idAlerta=id;
	
}

function execEventShow(data){
	if (data != null && data.status == "success"){
		$('div.siouv_sombra_alerta').fadeIn(200);
		showDiv(idAlerta);
	}
}

var idModal;

function showModalAjax(id){
	idModal=id;
}

function hideModalAjax(id){
	idModal=id;
	
}

function execEventShowModal(data){
	
	if (data != null && data.status == "success"){
		$('div.siouv_sombra').fadeIn(200);
		showDiv(idModal);
		configuraTextArea();
		configuraDatePicker();
	}
}

function execEventHideModal(data){
	if (data != null && data.status == "success"){
		var strSeletor='#'+idModal;
		$('div.siouv_sombra').fadeOut(200);
		$(strSeletor).fadeOut(200);
	}
	limpaMsgBox();
}


function execEventHide(data){
	if (data != null && data.status == "success"){
		var strSeletor='#'+idAlerta;
		$('div.siouv_sombra_alerta').fadeOut(200);
		$(strSeletor).fadeOut(200);
	}
	limpaMsgBox();
}

function showAlerta(id){
	/* Mostra modal estilo alerta */
	$('div.siouv_sombra_alerta').fadeIn(200);
	showDiv(id);
}

function hideModal(id){
	/* Esconde modal estilo formulário */
	var strSeletor='#'+id;
	$('div.siouv_sombra').fadeOut(200);
	$(strSeletor).fadeOut(200);
	limpaMsgBox();
}

function hideAlerta(id){
	/* Esconde modal estilo alerta */
	var strSeletor='#'+id;
	$('div.siouv_sombra_alerta').fadeOut(200);
	$(strSeletor).fadeOut(200);
	limpaMsgBox();
}

function hideAlerta(id,data){
	/* Esconde modal estilo alerta, com overload para receber eventos */
	var strSeletor='#'+id;
	$('div.siouv_sombra_alerta').fadeOut(200);
	$(strSeletor).fadeOut(200);
}

function scrollTop(){
	/* Rola a tela para o topo */
	$('body,html').animate({scrollTop:0},400);
}

function clearInputForm(){
	/* Limpa formulário */
	$("form :input").each(function(){
		$(this).not(':checkbox, :radio, :button, :submit, :reset, :hidden').val('');
	});
	
	$("form :checkbox, :radio").each(function(){
		$(this).prop('checked', false);
	});
	
	$('select').val('0');
			 
}
function clearResults(){
	$('#datatableModel').hide();
	
	$('#scroller').hide();
	
}

function zeroPad(n){
	/* Escreve um número com dois algarismos, preenchendo com zero se for menor que 10 */
	return (n<10)?'0'+n.toString():n.toString();
}

function atualizaCalendario(id){
	/* Redesenha miolo do calendário */
	var intMesCal = $('#'+id+' .cal_mes').val();
	var intAnoCal = $('#'+id+' .cal_ano').val();
	var strMeses = ['Janeiro','Fevereiro','Mar&ccedil;o','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'];
	var intDias = [31,28,31,30,31,30,31,31,30,31,30,31];
	if(((intAnoCal % 4 == 0) && (intAnoCal % 100 != 0)) || (intAnoCal % 400 == 0)) {
		intDias[1]=29;
	}
	var strMesAtual = strMeses[intMesCal]+' '+intAnoCal;
	var numDiasMes = intDias[intMesCal];
	$('#'+id+' .cal_mes_atual').html(strMesAtual);
	$('#'+id+' tbody').empty();
	var dataInicio = new Date(intAnoCal, intMesCal, 1);
	var dataFim = new Date(intAnoCal, intMesCal, numDiasMes);
	var strCalendario = "";
	
	var diaInicio = -(dataInicio.getDay())+1;
	var diaFim = numDiasMes + (6-dataFim.getDay());
	
	var dia, strTitle, idCampo;
	
	intMesCal++;
	for (var i=diaInicio;i<diaFim;i=i+7){
		strCalendario += '<tr>';
		for (var j=0;j<7;j++){
			dia = i+j;
			if((dia>0)&&(dia<=numDiasMes)){
				strTitle = zeroPad(dia)+'/'+zeroPad(intMesCal)+'/'+intAnoCal;
				strCalendario += '<td class="dia" title="'+strTitle+'">'+dia+'</td>';
			} else {
				strCalendario += '<td></td>';
			}
		}
		strCalendario += '</tr>';
	}
	$('#'+id+' tbody').html(strCalendario);
	$('#'+id+' .dia').click(function(){
		idCampo = '[id="' + (id.slice(4)).replace('_-_',':') + '"]';
		$(idCampo).val($(this).attr('title'));
		$('#'+id).hide();
	});
}

function criaCalendario(id, offset){
	/* Cria um novo calendário */
	var calId='cal_'+id;
	if ($('#'+calId).length != 0) {
		$('#'+calId).remove();
	}
	var hoje = new Date();
	var intDiaAtual = hoje.getDate();
	var intMesAtual = hoje.getMonth();
	var intAnoAtual = hoje.getFullYear();
	var strCal;
	strCal ='<div class="caixa_calendario" id="'+calId+'">';
	strCal+='<input type="hidden" class="cal_dia" value="'+intDiaAtual+'" />';
	strCal+='<input type="hidden" class="cal_mes" value="'+intMesAtual+'" />';
	strCal+='<input type="hidden" class="cal_ano" value="'+intAnoAtual+'" />';
	strCal+='<table><caption><ul>';
	strCal+='<li class="cal_ano_ant" title="Ano anterior"></li>';
	strCal+='<li class="cal_mes_ant" title="M&ecirc;s anterior"></li>';
	strCal+='<li class="cal_mes_atual"></li>';
	strCal+='<li class="cal_mes_seg" title="M&ecirc;s seguinte"></li>';
	strCal+='<li class="cal_ano_seg" title="Ano seguinte"></li>';
	strCal+='<li class="cal_fechar" title="Fechar"></li>';
	strCal+='</ul></caption><thead><tr>';
	strCal+='<th>Dom</th><th>Seg</th><th>Ter</th><th>Qua</th><th>Qui</th><th>Sex</th><th>Sáb</th>';
	strCal+='</tr></thead><tbody>';
	strCal+='</tbody></table></div>';
	$('body').append(strCal);
	
	$('#'+calId+' .cal_fechar').click(function(){
//		$(this).parent().parent().parent().parent().hide();
		$('#'+calId).hide();
	});
	atualizaCalendario(calId);

	var selAno = '#'+calId+' .cal_ano';
	var selMes = '#'+calId+' .cal_mes';
	var intAno;
	var intMes;
	$(selAno+'_ant').click(function(){
		intAno = $(selAno).val();
		intAno--;
		$(selAno).val(intAno);
		atualizaCalendario(calId);
	});
	$(selAno+'_seg').click(function(){
		intAno = $(selAno).val();
		intAno++;
		$(selAno).val(intAno);
		atualizaCalendario(calId);
	});
	$(selMes+'_ant').click(function(){
		intMes = $(selMes).val();
		intAno = $(selAno).val();
		if (intMes == 0) {
			intMes = 12;
			intAno--;
			$(selAno).val(intAno);
		}
		intMes--;
		$(selMes).val(intMes);
		atualizaCalendario(calId);
	});
	$(selMes+'_seg').click(function(){
		intMes = $(selMes).val();
		intAno = $(selAno).val();
		if (intMes == 11) {
			intMes = -1;
			intAno++;
			$(selAno).val(intAno);
		}
		intMes++;
		$(selMes).val(intMes);
		atualizaCalendario(calId);
	});
	$('#'+calId).offset({top: offset.top+19, left: offset.left });
	$('#'+calId).hide();
	return calId;
}

function configuraDatePicker(){
	/* Configura campos de input com seleção de data */
	$('.caixa_data').mask('00/00/0000');
	$('.caixa_data').each(function(){
		var offset=$(this).offset();
		var strId=$(this).attr('id');
		if(strId==undefined){
			strId='cal_'+(Math.floor(Math.random()*10000)+1);
			$(this).attr('id',strId);
		} else {
			strId = strId.replace(':','_-_');
		}
		var calId='#'+criaCalendario(strId, offset);
		var calFind = strId+"_icon";
		if($("#"+calFind).length==0){
			$(this).after('&nbsp;<a href="#" id="' + calFind + '" title="Clique aqui para Selecionar Calend&aacute;rio"><img src="/siouv/resources/images/icons/calendar.png" /></a>&nbsp;');
		}
		$(this).next('a').click(function(e){
			$('.caixa_calendario').hide();
			$(calId).show();
			e.preventDefault();
		});
	});
}

function limpaMsgBox(){
	$('.siouv_msgbox').html('<div id="msg"></div><a href="javascript:limpaMsgBox();" class="siouv_msgbox_fechar" title="Fechar">Fechar</a>');
}

function escreveMsgBox(strMensagem){
	$('.siouv_msgbox').html('<ul id="msg"><li class="siouv_erro">' + strMensagem + '</li></ul><a href="javascript:limpaMsgBox();" class="siouv_msgbox_fechar" title="Fechar">Fechar</a>');
	$('.siouv_msgbox').show();
}

function configuraAutoScroll(){
	/* Configura textarea para rolar automaticamente dependendo do estado de um conjunto de radio buttons */
	if($('.autoscroll').length > 0){
		setInterval(function(){
			if($('#autoScroll_sim').is(':checked')){
				$('.autoscroll').each(function(){
					$(this).animate({ scrollTop: $(this)[0].scrollHeight}, 500);
				});
			};
		}, 1000);
	};
}

function showProcessando(){
	$('.siouv_processando').fadeIn(200);
}

function hideProcessando(){
	$('.siouv_processando').fadeOut(200);
}

$(document).ready(function(){
	$.noConflict();
	configuraTextArea();
	configuraDatePicker();
	$('.caixa_mes').mask('00/0000');
	configuraAutoScroll();
	$('body').on('click','table.collapsible caption',function(){
		$(this).siblings().toggle();
		$(this).toggleClass('aberta');
	});
});


$(window).resize(function(){
	reposicionaModais();
});