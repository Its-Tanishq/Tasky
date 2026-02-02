import React from 'react'

const Footer = () => {
    return (
        <footer className="py-16 bg-[var(--footer-bg)] border-t border-solid border-[var(--glass-border)] text-center text-[var(--text-secondary)] transition-all duration-300 ease-in-out">
            <div className="max-w-[1200px] mx-auto px-8">
                <p>&copy; {new Date().getFullYear()} Tasky. All rights reserved.</p>
            </div>
        </footer>
    )
}

export default Footer