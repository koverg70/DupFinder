application {
    title = 'DupFinder'
    startupGroups = ['dupFinder']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "dupFinder"
    'dupFinder' {
        model      = 'dupfinder.DupFinderModel'
        view       = 'dupfinder.DupFinderView'
        controller = 'dupfinder.DupFinderController'
    }

}
