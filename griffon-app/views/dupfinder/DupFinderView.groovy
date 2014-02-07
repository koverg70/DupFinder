package dupfinder

actions {
    action(id: 'openAction', name: 'Open XML', mnemonic: 'O', accelerator: shortcut('O'), closure: controller.openFile)
    action(id: 'parseAction', name: 'Parse Folder', mnemonic: 'P', accelerator: shortcut('P'), closure: controller.parseFolder)
    action(id: 'quitAction', name: 'Quit', mnemonic: 'Q', accelerator: shortcut('Q'), closure: controller.quit)
}

fileChooserWindow = fileChooser()
fileViewerWindow = application(title: 'DupFinder',
  preferredSize: [640, 480],
  pack: true,
  //location: [50,50],
  locationByPlatform: true,
  iconImage:   imageIcon('/griffon-icon-48x48.png').image,
  iconImages: [imageIcon('/griffon-icon-48x48.png').image,
               imageIcon('/griffon-icon-32x32.png').image,
               imageIcon('/griffon-icon-16x16.png').image]) {
    // add content here
    menuBar {
        menu('File') {
            menuItem openAction
            menuItem parseAction
            separator()
            menuItem quitAction
        }
    }
    borderLayout()
    panel(constraints: NORTH) {
        label('Folder name: ')
        label(text : bind { model.folderName })
    }
    scrollPane(constraints: CENTER) {
        table {
            def columns = ['Name', 'Path', 'Length']
            def tf = defaultTableFormat(columnNames: columns)
            eventTableModel(source: model.duplicates, format: tf)
            installTableComparatorChooser(source: model.duplicates)
        }
    }

    panel(constraints: SOUTH) {
        label(text : bind {model.footerMessage})
    }
}
