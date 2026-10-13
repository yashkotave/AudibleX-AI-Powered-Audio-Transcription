import Icon from './Icon.jsx'

function shortDate(value) {
  return new Intl.DateTimeFormat(undefined, { month: 'short', day: 'numeric', year: 'numeric' })
    .format(new Date(value))
}

export default function History({ items, loading, query, onQueryChange, onView, onDelete, error }) {
  return (
    <section className="history-section" id="history">
      <div className="history-heading">
        <div>
          <span className="eyebrow">YOUR LIBRARY</span>
          <h2>Recent transcriptions <span className="count-badge">{items.length}</span></h2>
        </div>
        <label className="search-box">
          <Icon name="search" size={17} />
          <input type="search" placeholder="Search transcriptions…" value={query}
            onChange={(event) => onQueryChange(event.target.value)} aria-label="Search transcriptions" />
          {query && <button type="button" aria-label="Clear search" onClick={() => onQueryChange('')}><Icon name="close" size={14} /></button>}
        </label>
      </div>
      {error && <div className="notice-error" role="alert">{error}</div>}
      {loading ? <div className="history-empty">Loading your library…</div> : items.length === 0 ? (
        <div className="history-empty">
          <span className="empty-icon"><Icon name="audio" size={22} /></span>
          <strong>{query ? 'No matches found' : 'Your library is ready'}</strong>
          <span>{query ? 'Try another title or keyword.' : 'Your completed transcriptions will appear here.'}</span>
        </div>
      ) : (
        <div className="history-list">
          {items.map((item) => (
            <article className="history-item" key={item.id}>
              <span className="history-file-icon"><Icon name="audio" size={18} /></span>
              <button type="button" className="history-main" onClick={() => onView(item)}>
                <strong>{item.title}</strong>
                <span>{shortDate(item.createdAt)} <i /> {item.transcript.length.toLocaleString()} characters</span>
              </button>
              <span className="history-status"><span /> Complete</span>
              <button type="button" className="icon-button delete-button" aria-label={`Delete ${item.title}`}
                onClick={() => onDelete(item)}><Icon name="trash" size={17} /></button>
              <button type="button" className="view-arrow" aria-label={`View ${item.title}`}
                onClick={() => onView(item)}><Icon name="chevron" size={17} /></button>
            </article>
          ))}
        </div>
      )}
    </section>
  )
}
