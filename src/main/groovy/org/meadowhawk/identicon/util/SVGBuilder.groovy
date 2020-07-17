package org.meadowhawk.identicon.util

import groovy.xml.MarkupBuilder

import java.awt.Color

class SVGBuilder {
    static createSvg(File outputFile, int width, int height, Closure content) {
        def writer = new StringWriter()
        def markupBuilder = new MarkupBuilder(writer)

        content.setDelegate(markupBuilder)
        markupBuilder.svg(width:width,height:height, xmlns:'http://www.w3.org/2000/svg', 'xmlns:xlink':'http://www.w3.org/1999/xlink', version:'1.1') {
            content(width, height)
        }
        outputFile.withWriter {
            it << writer.toString()
        }
    }

    static createSvg(StringWriter writer, int width, int height, Closure content) {
        def markupBuilder = new MarkupBuilder(writer)
        content.setDelegate(markupBuilder)
        markupBuilder.svg(width:width,height:height, xmlns:'http://www.w3.org/2000/svg', 'xmlns:xlink':'http://www.w3.org/1999/xlink', version:'1.1') {
            content(width, height)
        }
    }

    static def hsbColor = { hue, saturation = 1.0f, brightness = 1.0f ->
        def color = java.awt.Color.getHSBColor(hue as float, saturation as float, brightness as float)
        if(hue == "0") //overrideWith White
            color = java.awt.Color.WHITE
        return "rgb(${color.red},${color.green},${color.blue})"
    }

    static def rbgColor = { Color color ->
        return "rgb(${color.red},${color.green},${color.blue})"
    }

    static  void generateCircles(Writer writer, byte[] bytes, Pattern pattern, int width, int height){
        int colorCt = width
        String[] colors = pattern.fillColors(bytes, colorCt)
        String bgColor = (bytes[2]%2==0)?"ffffff": "282828"
        SVGBuilder.createSvg(writer, width, height) { w, h ->
            rect(x: 0, y: 0, width: w, height: h, fill: pattern.buildColorValue(bgColor)) //White backfill
            colors.each { color->
                circle(cx:Math.random() * width, cy:Math.random() * height,r:Math.min(width,height)/ (Math.abs(new Random().nextInt() % (25 - 10)) + 10) , fill:pattern.buildColorValue(color))
            }
        }
    }

    static void generateGrid(Writer writer, byte[] bytes, Pattern pattern, int width, int height){
        int colorCt = width * height
        String[] colors = pattern.fillColors(bytes, colorCt)
        SVGBuilder.createSvg(writer, width, height) { w, h ->
            int x = 0
            int y = 0
            int b = 0
            while (y < h) {
                while (x < w) {
                    rect(x: x, y: y, width: 10, height: 10, fill: pattern.buildColorValue(colors[b]))
                    x += 10
                    b++
                }
                y += 10
                x = 0
            }
        }
    }
}
