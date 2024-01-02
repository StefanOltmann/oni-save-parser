/*
 * ONI Save Parser
 * Copyright (C) 2024 Stefan Oltmann
 * https://stefan-oltmann.de/oni-save-parser
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import de.stefan_oltmann.oni.parser.SaveGameReader
import de.stefan_oltmann.oni.parser.createSummary
import de.stefan_oltmann.oni.parser.toByteArray
import kotlinx.browser.document
import kotlinx.dom.appendElement
import kotlinx.dom.appendText
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.w3c.dom.DragEvent
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.get
import org.w3c.files.File
import org.w3c.files.FileReader
import org.w3c.files.get

private var uploadsElement: Element? = null

fun main() {

    uploadsElement = document.getElementById("uploads")

    registerFileInputEvents()
}

private fun registerFileInputEvents() {

    val dropbox = document.getElementById("dropbox")
    val fileInput = document.getElementById("fileInput") as? HTMLElement

    dropbox?.addEventListener("dragover") { event ->

        event as DragEvent

        event.preventDefault()
        event.dataTransfer?.dropEffect = "copy"
        dropbox.classList.add("highlight")
    }

    dropbox?.addEventListener("dragleave") { event ->

        event as DragEvent

        event.preventDefault()
        dropbox.classList.remove("highlight")
    }

    dropbox?.addEventListener("drop") { event ->

        event as DragEvent

        event.preventDefault();
        dropbox.classList.remove("highlight");

        val items = event.dataTransfer?.items;

        if (items == null || items.length == 0)
            return@addEventListener

        for (i in 0..<items.length)
            handleFile(items[i]!!.getAsFile()!!)
    }

    dropbox?.addEventListener("click") { _ ->
        fileInput?.click()
    }

    fileInput?.addEventListener("change") { event ->

        val target = event.target as? HTMLInputElement ?: return@addEventListener

        val files = target.files

        if (files == null || files.length == 0)
            return@addEventListener

        for (i in 0..<files.length)
            handleFile(files[i]!!)
    }
}

private fun handleFile(file: File) {

    val fileReader = FileReader()

    fileReader.onload = { event ->

        val target = event.target as? FileReader

        if (target != null) {

            val arrayBuffer = target.result as? ArrayBuffer

            if (arrayBuffer != null) {

                val uInt8Bytes = Uint8Array(arrayBuffer)

                processFile(file.name, uInt8Bytes)
            }
        }
    }

    fileReader.readAsArrayBuffer(file)
}

private fun processFile(fileName: String, uint8Array: Uint8Array) {

    try {

        val bytes = uint8Array.toByteArray()

        val saveGame = SaveGameReader.readSaveGame(bytes)

        val summary = saveGame.createSummary()

        appendUploadBox(
            fileName = fileName,
            statusMessage = null,
            summaryHtmlContent = summary.toString().toHtmlString(),
            success = true
        )

    } catch (ex: Throwable) {

        /* Catching throwable to not lose errors. */

        val message = ex.message ?: "Unknown error."

        appendUploadBox(
            fileName = fileName,
            statusMessage = "Failed to process file.",
            summaryHtmlContent = message,
            success = false
        )

        ex.printStackTrace()
    }
}

private fun appendUploadBox(
    fileName: String,
    statusMessage: String?,
    summaryHtmlContent: String,
    success: Boolean
) {

    val box = uploadsElement!!.appendElement("div") {

        className = "box"

    } as HTMLDivElement

    val boxTitle = box.appendElement("div") {

        className = "box-title"

        if (success)
            classList.toggle("successBackground", true)
        else
            classList.toggle("errorBackground", true)

        appendText(fileName)

        appendElement("i") {
            className = "fas fa-chevron-down expand-icon"
        }

    } as HTMLDivElement

    val boxContent = box.appendElement("div") {

        className = "box-content"

    } as HTMLDivElement

    if (statusMessage != null) {

        boxContent.appendElement("div") {

            className = "status-message"

            appendText(statusMessage)
        }
    }

    boxContent.appendElement("div") {

        className = "savegame-summary"

        innerHTML = summaryHtmlContent
    }

    boxTitle.onclick = {

        boxContent.style.display = if (boxContent.style.display == "none")
            "block"
        else
            "none"

        box.classList.toggle(
            token = "collapsed",
            force = boxContent.style.display == "none"
        )
    }

    /* Initially collapsed. */
    boxContent.style.display = "none"
    box.classList.toggle("collapsed", true)
}

private fun String.toHtmlString(): String =
    this.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace(" ", "&nbsp;")
        .replace("\n", "<br>")
