import Icon from './Icon.jsx'

const links = [
  { label: 'Home', icon: 'home' },
  { label: 'Transcriptions', icon: 'audio' },
  { label: 'About', icon: 'info' },
]

export default function Sidebar({ activePage, onNavigate }) {
  return (
    <aside className="sidebar">
      <a className="brand" href="#" onClick={(event) => { event.preventDefault(); onNavigate('Home') }}>
        <span className="brand-mark"><span /></span>
        <span>audible<span className="brand-accent">x</span></span>
      </a>
      <div className="nav-caption">WORKSPACE</div>
      <nav aria-label="Main navigation">
        {links.map((link) => (
          <button key={link.label} className={`nav-link ${activePage === link.label ? 'active' : ''}`}
            onClick={() => onNavigate(link.label)}>
            <Icon name={link.icon} size={19} />
            <span>{link.label}</span>
            {activePage === link.label && <span className="nav-active-dot" />}
          </button>
        ))}
      </nav>
      <div className="sidebar-bottom">
        <div className="sidebar-card">
          <div className="sidebar-card-icon"><Icon name="audio" size={17} /></div>
          <strong>Your words, in focus.</strong>
          <p>Transform your audio into searchable, shareable text.</p>
        </div>
        <div className="profile-row">
          <div className="avatar">AX</div>
          <div><strong>Personal workspace</strong><span>Free plan</span></div>
          <span className="profile-menu">···</span>
        </div>
      </div>
    </aside>
  )
}
