function getNewSelectedElementText(fieldName, fieldValue, displayText, isLeftAligned, imagePath, callBackAfterSelection) {
    var str = "";
    var displayTextModified = "<span>" + displayText + "</span>";

    if (callBackAfterSelection.length) {
        var functionCallStatement = callBackAfterSelection + "('" + fieldValue + "', '" + displayText + "')";
        displayTextModified += eval(functionCallStatement);
    }

    if (isLeftAligned) {
        str = '<li><input type="hidden" name="' + fieldName + '" value="' + fieldValue +
              '"/> <a class="removeLink"><img class="removeButtonImage"  src="' + imagePath + '" border="0" /></a>'
                + displayTextModified + ' </li>';
    } else {
        str = '<li><input type="hidden" name="'
                + fieldName + '" value="' + fieldValue + '"/>' + displayTextModified +
              '<a class="removeLink">&nbsp;</a> </li>';
    }
    return str;
}

function removeMe(image) {
    var liElement = jQuery(image).parent('li');
    var spanElement = jQuery(image).parent('li').children('span:first');
    var optionText = jQuery.trim(spanElement.text());
    var optionValue = jQuery(image).siblings('input:hidden:first').val();
    var selectElement = jQuery(image).parent('li').parent('ul').siblings('select')[0];
    selectElement.options[selectElement.length] = new Option(optionText, optionValue, false, false);
    liElement.remove();
    return false;
}

function updateSelectBox(multiSelectId, isLeftAligned, imagePath, callBackAfterSelection) {
    isLeftAligned = (isLeftAligned == "true");
    var v = jQuery('#' + multiSelectId + '-select').val();
    if (v.length < 1) return;
    var t = jQuery('#' + multiSelectId + '-select option:selected').text();
    jQuery('#' + multiSelectId + '-ul').append(getNewSelectedElementText(multiSelectId, v, t, isLeftAligned, imagePath, callBackAfterSelection));
    jQuery('#' + multiSelectId + '-select option[value=' + v + ']').remove();
    jQuery('#' + multiSelectId + '-ul>li:last>a.removeLink').click(function() {
        removeMe(this);
    });
}

function disableMultiSelect(multiSelectId) {
    jQuery('#' + multiSelectId + '-ul>li>a>img.removeButtonImage').css('opacity', '0.2');
    jQuery('#' + multiSelectId + '-ul>li>a.removeLink').unbind('click');
    jQuery('#' + multiSelectId + '-select').attr('disabled', true);
}

function enableMultiSelect(multiSelectId) {
    jQuery('#' + multiSelectId + '-ul>li>a>img.removeButtonImage').css('opacity', '1');
    jQuery('#' + multiSelectId + '-ul>li>a.removeLink').bind('click', function() {
        removeMe(this);
    });
    jQuery('#' + multiSelectId + '-select').removeAttr('disabled');
}

function selectedCount(multiSelectId) {
    return jQuery('#' + multiSelectId + '-ul>li').size();
}

function deselectedCount(multiSelectId) {
    //    return total deselected items
    return jQuery('#' + multiSelectId + '-select option[value!=""]').size()
}

function selectedItems(multiSelectId) {
    /*
     var key,value,temp = [];
     jQuery('#' + multiSelectId + '-ul>li').each(function() {
     key = jQuery.trim(jQuery(this).children('span:first').text());
     value = jQuery.trim(jQuery(this).children('input:hidden:first').val());
     temp.push({'key':key,'value':value});
     });
     return temp;
     */
}
function deselectedItems(multiSelectId) {

    // return JSON
}

