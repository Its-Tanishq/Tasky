import React, { useState, useContext, useEffect, useRef } from 'react'
import { Sun, Moon, ChevronDown, Building2, User, Menu, X, UserStar } from 'lucide-react';
import { ThemeContext } from '../Context/ThemeContext';

const NavLink = ({ href, children, mobile }) => (
    <li className={mobile ? 'block w-full' : ''}>
        <a
            href={href}
            className={`cursor-pointer transition-all duration-300 ease-in-out inline-block ${mobile
                ? 'block w-full py-3 px-4 rounded-lg hover:bg-[var(--primary-color)] hover:text-white hover:pl-6 text-[var(--text-color)] font-medium'
                : 'hover:text-[var(--primary-color)] hover:-translate-y-[2px] hover:text-shadow-[0_6px_20px_rgba(108,92,231,0.6)]'
                }`}
        >
            {children}
        </a>
    </li>
);

const navLinks = [
    { name: 'Features', href: '#features' },
    { name: 'About', href: '#about' }
];

const Navbar = () => {
    const { theme, toggleTheme } = useContext(ThemeContext);
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

    useEffect(() => {
        const handleResize = () => {
            if (window.innerWidth >= 768) {
                setIsMobileMenuOpen(false);
            }
        };
        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    const timeoutRef = useRef(null);

    const handleMouseEnter = () => {
        if (timeoutRef.current) {
            clearTimeout(timeoutRef.current);
        }
        setIsDropdownOpen(true);
    };

    const handleMouseLeave = () => {
        timeoutRef.current = setTimeout(() => {
            setIsDropdownOpen(false);
        }, 300);
    };

    return (
        <nav className='top-0 w-full z-50 transition-all duration-300 pt-6 pt-[1.5rem]'>
            <div className='flex justify-between md:justify-around items-center px-6 md:px-0 container mx-auto'>
                <div className="text-[1.5rem] font-bold tracking-tight cursor-pointer">Tasky</div>

                {/* Desktop Menu */}
                <ul className='hidden md:flex gap-10 text-[1rem] list-none items-center'>
                    {navLinks.map(link => (
                        <NavLink key={link.name} href={link.href}>{link.name}</NavLink>
                    ))}

                    <li>
                        <button
                            onClick={toggleTheme}
                            className="p-2 rounded-full w-[40px] h-[40px] inline-flex items-center justify-center border border-[var(--glass-border)] bg-[var(--glass-bg)] text-[var(--text-color)] cursor-pointer hover:bg-[var(--glass-border)] transition-colors duration-300"
                        >
                            {theme === 'dark' ? <Sun size={20} /> : <Moon size={20} />}
                        </button>
                    </li>

                    <li
                        className="relative"
                        onMouseEnter={handleMouseEnter}
                        onMouseLeave={handleMouseLeave}
                    >
                        <button className='flex items-center gap-2 bg-transparent border-2 border-solid border-[var(--secondary-color)] px-6 py-2 rounded-full text-[var(--secondary-color)] hover:bg-[var(--secondary-color)] hover:text-[#000] cursor-pointer transition-all duration-300 font-medium'>
                            Get Started <ChevronDown size={16} />
                        </button>

                        <div className={`absolute right-0 top-full w-64 pt-2 transition-all duration-300 transform origin-top-right z-50 ${isDropdownOpen ? 'opacity-100 scale-100 translate-y-0 pointer-events-auto' : 'opacity-0 scale-95 -translate-y-2 pointer-events-none'}`}>
                            <div className="bg-[var(--card-bg)] border border-[var(--glass-border)] rounded-xl shadow-lg overflow-hidden">
                                <div className="p-2 flex flex-col gap-1">
                                    <a href="#create-org" className="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-[var(--ui-element-bg)] transition-colors text-[var(--text-color)] no-underline group">
                                        <div className="p-2 rounded-md bg-purple-500/10 text-purple-500 group-hover:bg-purple-500 group-hover:text-white transition-colors">
                                            <Building2 size={20} />
                                        </div>
                                        <div className="flex flex-col">
                                            <span className="font-medium text-sm">Create Organization</span>
                                            <span className="text-xs text-[var(--text-secondary)]">For Organization Owner</span>
                                        </div>
                                    </a>
                                    <a href="#login-org" className="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-[var(--ui-element-bg)] transition-colors text-[var(--text-color)] no-underline group">
                                        <div className="p-2 rounded-md bg-sky-500/10 text-sky-500 group-hover:bg-sky-500 group-hover:text-white transition-colors">
                                            <UserStar size={20} />
                                        </div>
                                        <div className="flex flex-col">
                                            <span className="font-medium text-sm">Login as Organization Owner</span>
                                            <span className="text-xs text-[var(--text-secondary)]">For Organization Owner</span>
                                        </div>
                                    </a>
                                    <a href="#login-employee" className="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-[var(--ui-element-bg)] transition-colors text-[var(--text-color)] no-underline group">
                                        <div className="p-2 rounded-md bg-teal-500/10 text-teal-500 group-hover:bg-teal-500 group-hover:text-white transition-colors">
                                            <User size={20} />
                                        </div>
                                        <div className="flex flex-col">
                                            <span className="font-medium text-sm">Login as Employee</span>
                                            <span className="text-xs text-[var(--text-secondary)]">For Employees</span>
                                        </div>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </li>
                </ul>

                <button
                    className="md:hidden p-2 text-[var(--text-color)]"
                    onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                >
                    {isMobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
                </button>
            </div>

            <div className={`md:hidden overflow-hidden transition-all duration-300 ${isMobileMenuOpen ? 'max-h-[500px] opacity-100 mt-4' : 'max-h-0 opacity-0'}`}>
                <div className="bg-[var(--card-bg)] border-t border-[var(--glass-border)] p-4 flex flex-col gap-4 shadow-xl">
                    <ul className='flex flex-col gap-2 list-none p-0 m-0'>
                        {navLinks.map(link => (
                            <NavLink key={link.name} href={link.href} mobile={true}>{link.name}</NavLink>
                        ))}
                    </ul>

                    <div className="flex justify-between items-center px-4 py-2 bg-[var(--ui-element-bg)] rounded-xl">
                        <span className="text-[var(--text-color)] font-medium">Theme</span>
                        <button
                            onClick={toggleTheme}
                            className="p-2 rounded-full w-[40px] h-[40px] inline-flex items-center justify-center border border-[var(--glass-border)] bg-[var(--glass-bg)] text-[var(--text-color)] cursor-pointer"
                        >
                            {theme === 'dark' ? <Sun size={20} /> : <Moon size={20} />}
                        </button>
                    </div>

                    <div className="flex flex-col gap-2 mt-2">
                        <a href="#create-org" className="flex items-center gap-3 px-4 py-3 rounded-lg bg-[var(--ui-element-bg)] hover:bg-[var(--primary-color)] hover:text-white transition-colors text-[var(--text-color)] no-underline group">
                            <Building2 size={20} />
                            <div className="flex flex-col">
                                <span className="font-medium text-sm">Create Organization</span>
                            </div>
                        </a>
                        <a href="#login-org" className="flex items-center gap-3 px-4 py-3 rounded-lg border border-[var(--secondary-color)] text-[var(--secondary-color)] hover:bg-[var(--secondary-color)] hover:text-black transition-colors no-underline group">
                            <UserStar size={20} />
                            <div className="flex flex-col">
                                <span className="font-medium text-sm">Login as Organization Owner</span>
                            </div>
                        </a>
                        <a href="#login-employee" className="flex items-center gap-3 px-4 py-3 rounded-lg border border-[var(--secondary-color)] text-[var(--secondary-color)] hover:bg-[var(--secondary-color)] hover:text-black transition-colors no-underline group">
                            <User size={20} />
                            <div className="flex flex-col">
                                <span className="font-medium text-sm">Login as Employee</span>
                            </div>
                        </a>
                    </div>
                </div>
            </div>
        </nav>
    )
}

export default Navbar