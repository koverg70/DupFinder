package dupfinder

import groovy.beans.Bindable
import griffon.transform.PropertyListener
import ca.odell.glazedlists.EventList
import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.SortedList

class DupFinderModel
{
    @Bindable String folderName = 'N/A'

    @Bindable String footerMessage = " "

    List<FileDesc> files = []

    EventList duplicates = new SortedList(new BasicEventList(),
            {a, b -> a.name <=> b.name} as Comparator)

}