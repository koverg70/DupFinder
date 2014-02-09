package dupfinder

import javax.swing.JFileChooser
import java.text.SimpleDateFormat

class DupFinderController {
    // these will be injected by Griffon
    def model
    def view

    // void mvcGroupInit(Map args) {
    //    // this method is called after model and view are injected
    // }

    // void mvcGroupDestroy() {
    //    // this method is called when the group is destroyed
    // }

    /*
        Remember that actions will be called outside of the UI thread
        by default. You can change this setting of course.
        Please read chapter 9 of the Griffon Guide to know more.
       
    def action = { evt = null ->
    }
    */
    private void updateFooter()
    {
        def dups = Utils.computeDuplicates model.files
        def duplensum = 0
        def filelensum = 0
        model.duplicates.withReadLock {
            model.duplicates.clear()
            model.files.each {
                filelensum += it.length
            }
            dups.each {
                def v = [name: it[0].name, path: it[0].path, length: it[0].length, descrs: it, dupes:it.size()]
                duplensum += it[0].length
                model.duplicates.add v
            }
        }
        duplensum = Utils.convertToStringRepresentation(duplensum)
        filelensum = Utils.convertToStringRepresentation(filelensum)
        model.footerMessage = "Total number of files: ${model.files.size()} (${filelensum}) / Duplicates: ${model.duplicates.size()} (${duplensum})"
    }

    def openFile = {
        view.fileChooserWindow.fileSelectionMode = JFileChooser.FILES_ONLY
        view.fileChooserWindow.dialogType = JFileChooser.OPEN_DIALOG
        def openResult = view.fileChooserWindow.showOpenDialog()
        if( JFileChooser.APPROVE_OPTION == openResult ) {
            def xmlFile = view.fileChooserWindow.getSelectedFile()
            model.folderName = xmlFile.name

            model.duplicates.clear()

            def df = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", new Locale("EN", "US"))
            println "Reading " + xmlFile
            def xml = new XmlSlurper().parse(xmlFile);

            println "Processing " + xmlFile
            println "Size " + xml.FileDesc.size()
            int count = 0
            def descrs = []
            xml.FileDesc.each {
                descrs << new FileDesc(
                        path: it.path,
                        name: it.name,
                        modified: df.parse(it.modified.toString()),
                        length: Long.parseLong(it.length.toString()),
                        md5Hash: it.md5Hash.toString()
                )

                if (++count % 100 == 0) {
                    model.footerMessage = "Processed: ${count}"
                    //model.files.withReadLock {
                        model.files.addAll descrs
                        descrs.clear()
                    //}
                }
            }
            //model.files.withReadLock {
                model.files.addAll descrs
                descrs.clear()
            //}

            updateFooter()
        }
    }

    def parseFolder = {
        view.fileChooserWindow.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
        view.fileChooserWindow.dialogType = JFileChooser.OPEN_DIALOG
        def openResult = view.fileChooserWindow.showOpenDialog()
        if( JFileChooser.APPROVE_OPTION == openResult ) {
            def dir = view.fileChooserWindow.getSelectedFile()
            model.folderName = dir.path
            int count = 0
            def descrs = []
            println "Processing folder " + dir
            FileDesc.processFolder dir, {
                descrs << it
                if (++count % 100 == 0) {
                    model.footerMessage = "Processed: ${count}"
                    //model.files.withReadLock {
                        model.files.addAll descrs
                        descrs.clear()
                    //}
                }
            }
            //model.files.withReadLock {
                model.files.addAll descrs
                descrs.clear()
            //}

            updateFooter()
        }
    }

    def saveFileAs = {
        view.fileChooserWindow.setFileSelectionMode(JFileChooser.FILES_ONLY)
        view.fileChooserWindow.dialogType = JFileChooser.SAVE_DIALOG
        def openResult = view.fileChooserWindow.showSaveDialog()
        if( JFileChooser.APPROVE_OPTION == openResult ) {
            def out = view.fileChooserWindow.selectedFile
            println "Writing output to " + out
            out.delete()
            out << "<?xml version=\"1.0\" encoding=\"ISO-8859-2\" ?>"
            out << "<FileList>"
            model.files.each {
                out << Utils.pogoToXml(it)
            }
            out << "</FileList>"
        }
    }

    def clear = {
        model.files.clear()
        model.duplicates.clear()
        model.footerMessage = " "
        model.folderName = "N/A"
    }

    def quit = {
        app.shutdown()
    }
}
