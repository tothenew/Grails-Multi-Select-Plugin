package com.intelligrape.multiSelect

import org.springframework.web.servlet.support.RequestContextUtils as RCU
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler

class MultiSelectTagLib {
    static namespace = "ui"

    def resources = {attrs ->
        def writer = out
        Boolean includeJQuery = Boolean.valueOf(attrs.remove('includeJQuery'))
        writer << """<link rel="stylesheet" href="${createLinkTo(dir: pluginContextPath, file: 'css/multi.select.css')}"/>"""
        if(includeJQuery){
			writer<<javascript(library:"jquery", plugin:"jquery")
        }
        writer << """<script type="text/javascript" src="${createLinkTo(dir: pluginContextPath, file: 'js/multi.select.js')}"></script>"""
    }

    def multiSelect = {attrs ->
        def writer = out
        def value = attrs.value
        def multiple = attrs.remove('multiple')

        if (value instanceof Collection && multiple == null) {
            multiple = 'multiple'
        }

        if (!multiple) {
            //use application select tag and exit
            writer << g.select(attrs)
            return
        }

        def disabled = attrs.remove('disabled')
        attrs.remove('value')

        Boolean isLeftAligned = Boolean.valueOf(attrs.remove('isLeftAligned'))
        String ulClass = isLeftAligned ? "selected-items-left-aligned" : "selected-items"
        def name = attrs.get('name')
        def keys = attrs.get('keys')
        def optionKey = attrs.get('optionKey')
        def optionValue = attrs.get('optionValue')
        def valueMessagePrefix = attrs.get('valueMessagePrefix')
        def messageSource = grailsAttributes.getApplicationContext().getBean("messageSource")
        def locale = RCU.getLocale(request)

        def remainingList = attrs.from - value
        attrs.from = remainingList

        def modifySelectedItems = attrs.remove('modifySelectedItems')
        attrs.name = "${attrs.name}-select"

        String callBackAfterSelection = attrs.remove('callBackAfterSelection')
        if (!callBackAfterSelection) callBackAfterSelection = ""

        String crossImagePath = resource(dir: pluginContextPath, file: 'images/cross.gif')

        // output using application select taglib for all de-selected items
        writer << g.select(attrs)

        // output all initially selected items as HTML ul-li elements
        writer << """<ul id="${name}-ul" class="${ulClass}">"""
        if (value) {
            value.eachWithIndex {el, i ->
                def keyValue = null
                def displayValue = null
                if (keys) {
                    keyValue = keys[i]
                }
                else if (optionKey) {
                    if (optionKey instanceof Closure) {
                        keyValue = optionKey(el)
                    }
                    else if (el != null && optionKey == 'id' && grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, el.getClass().name)) {
                        keyValue = el.ident()
                    }
                    else {
                        keyValue = el[optionKey]
                    }
                }
                else {
                    keyValue = el
                }
                if (optionValue) {
                    if (optionValue instanceof Closure) {
                        displayValue = optionValue(el).toString().encodeAsHTML()
                    }
                    else {
                        displayValue = el[optionValue].toString().encodeAsHTML()
                    }
                }
                else if (valueMessagePrefix) {
                    def message = messageSource.getMessage("${valueMessagePrefix}.${keyValue}", null, null, locale)
                    if (message != null) {
                        displayValue = message.encodeAsHTML()
                    }
                    else if (keyValue) {
                        displayValue = keyValue.encodeAsHTML()
                    }
                    else {
                        def s = el.toString()
                        if (s) displayValue = s.encodeAsHTML()
                    }
                }
                else {
                    def s = el.toString()
                    if (s) displayValue = s.encodeAsHTML()
                }

                writer.println()

                if (modifySelectedItems && modifySelectedItems instanceof Closure) {
                    displayValue = "<span>" + displayValue + "</span>" + modifySelectedItems(keyValue, displayValue)
                } else {
                    displayValue = "<span>" + displayValue + "</span>"
                }
                writer << "<li>"
                writer << g.hiddenField(name: name, value: keyValue)
                if (isLeftAligned) {
                    writer << '<a class="removeLink">'
                    writer << """<img class="removeButtonImage" src="${crossImagePath}" border="0"/>"""
                    writer << '</a>'
                    writer << displayValue
                } else {
                    writer << displayValue
                    writer << '<a  class="removeLink">&nbsp;</a>'
                }
                writer << "</li>"

            }
        }

        writer << "</ul>"
        writer << """
        <script type="text/javascript">
        jQuery(document).ready(function() {
            jQuery("#${name}-select").bind("change", function() {
                updateSelectBox("${name}", "${isLeftAligned}", "${crossImagePath}","$callBackAfterSelection");
            });
           """
        if (Boolean.valueOf(disabled)) {
            out << """ disableMultiSelect("${name}"); """
        } else {
            out << """ enableMultiSelect("${name}"); """
        }
        writer << "});"
        writer << "</script>"
    }

}
