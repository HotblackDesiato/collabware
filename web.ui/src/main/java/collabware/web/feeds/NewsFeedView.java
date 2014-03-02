package collabware.web.feeds;


public class NewsFeedView 
{
//	extends AbstractAtomFeedView {
//
//	@Override
//	protected List<Entry> buildFeedEntries(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
//			throws Exception {
//		List<Change> changes = (List<Change>) model.get("changes");
//		List<Entry> entries = new ArrayList<Entry>(changes.size());
//		for (Change change: changes) {
//			entries.add(entryForChange(change));
//		}
//		return entries ;
//	}
//
//	private Entry entryForChange(Change change) {
//		Entry entry = new Entry();
//		entry.setId(UUID.randomUUID().toString());
//		entry.setTitle(change.getDescription());
//		entry.setUpdated(change.getDateTime());
//		entry.setAuthors(author(change));
//		entry.setAlternateLinks(link(change));
//		entry.setSummary(summary(change));
//		return entry;
//	}
//
//	private Content summary(Change change) {
//		Content content = new Content();
//		content.setType("text");
//		content.setValue(change.getDescription());
//		return content;
//	}
//
//	private List link(Change change) {
//		Link link = new Link();
//		link.setHref("/collabware/documents/show/"+change.getCollaboration().getId());	
//		return Arrays.asList(link);
//	}
//
//	private List author(Change change) {
//		Person author = new Person();
//		author.setName(change.getUser().getDisplayName());
//		return Arrays.asList(author);
//	}


}
