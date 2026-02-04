import React, { useEffect, useState } from 'react'
import Navbar from '../components/Navbar'
import Footer from '../components/Footer'
import Auth from '../components/Auth'
import { Activity, Users } from 'lucide-react'

const features = [
    {
        icon: Users,
        title: "Role-Based Access",
        description: "Granular permission controls to ensure the right people have the right access."
    },
    {
        icon: Activity,
        title: "Real-time Sync",
        description: "Watch updates happen instantly across your team as tasks are completed and moved."
    },
]

const Feature = ({ icon: Icon, title, description }) => (
    <div className='bg-[var(--card-bg)] p-8 md:p-10 rounded-[16px] border border-transparent transition-all duration-300 ease-in-out hover:-translate-y-[10px] hover:border-[var(--primary-color)]'>
        <div className='text-[2.0rem] md:text-[2.5rem] mb-6 inline-block p-4 bg-[rgba(108,92,231,0.1)] rounded-[12px] text-[var(--primary-color)] transition-colors duration-300 ease-in-out'>
            <Icon />
        </div>
        <h3 className='text-xl md:text-[1.5rem] mb-4'>{title}</h3>
        <p className='text-[var(--text-secondary)]'>{description}</p>
    </div>
)

const Landing = () => {
    const [isAuthOpen, setIsAuthOpen] = useState(false);
    const [authView, setAuthView] = useState('initial');

    const handleAuthOpen = (view) => {
        const targetView = typeof view === 'string' ? view : 'initial';
        setAuthView(targetView);
        setIsAuthOpen(true);
    };

    const handleAuthClose = () => {
        setIsAuthOpen(false);
        setTimeout(() => setAuthView('initial'), 300);
    };

    return (
        <div className='landing'>
            <Navbar onAuthOpen={handleAuthOpen} />
            <Auth isOpen={isAuthOpen} onClose={handleAuthClose} initialView={authView} />

            <section className='min-h-screen flex items-center relative overflow-hidden py-20 lg:py-0'>
                <div className='grid grid-cols-1 lg:grid-cols-2 gap-12 lg:gap-16 items-center max-w-[1200px] mx-auto px-6 md:px-8'>
                    <div className='flex flex-col items-center text-center lg:items-start lg:text-left'>
                        <h1 className='text-4xl md:text-5xl lg:text-[4rem] leading-[1.1] mb-6 bg-gradient-to-r from-[var(--heading-gradient-from)] to-[var(--heading-gradient-to)] bg-clip-text text-transparent'>
                            Orchestrate Your <br className='hidden lg:block' />Team's Harmony
                        </h1>
                        <p className='text-lg md:text-xl text-[var(--text-secondary)] mb-10 max-w-[500px]'>
                            The ultimate multi-organization platform for streamlining workflows,
                            managing roles, and boosting productivity across your entire enterprise.
                        </p>
                        <div className='flex gap-4'>
                            <button className='btn btn-primary' onClick={handleAuthOpen}>Get Started</button>
                        </div>
                    </div>
                    <div className='relative w-full max-w-[500px] lg:max-w-none mx-auto'>
                        <div className='bg-[var(--glass-bg)] backdrop-blur-[12px] border border-solid border-[var(--glass-border)] rounded-[20px] p-6 md:p-8 shadow-[0_8px_32px_0_var(--shadow-color)] [transform:perspective(1000px)_rotateY(-5deg)_rotateX(5deg)] transition-[transform,background-color,border-color] duration-500 ease-in-out hover:[transform:perspective(1000px)_rotateY(0)_rotateX(0)]'>
                            <div className="flex gap-4 mb-6 items-center">
                                <div className="w-10 h-10 rounded-full bg-[linear-gradient(135deg,#6c5ce7,#a29bfe)]"></div>
                                <div className="flex-1">
                                    <div className="h-[10px] w-[60%] bg-[var(--ui-element-bg)] rounded-[5px] mb-2"></div>
                                    <div className="h-[8px] w-[40%] bg-[var(--ui-element-bg-secondary)] rounded-[4px]"></div>
                                </div>
                            </div>
                            <div className="grid grid-cols-2 gap-4">
                                <div className="h-[80px] md:h-[100px] bg-[var(--ui-element-bg-secondary)] rounded-[12px] border border-[var(--ui-element-border)]"></div>
                                <div className="h-[80px] md:h-[100px] bg-[var(--ui-element-bg-secondary)] rounded-[12px] border border-[var(--ui-element-border)]"></div>
                                <div className="h-[60px] md:h-[80px] bg-[var(--ui-element-bg-secondary)] rounded-[12px] col-span-2 border border-[var(--ui-element-border)]"></div>
                            </div>
                            <div className="absolute -bottom-[20px] -right-[10px] md:-right-[20px] bg-[var(--card-bg)] text-[var(--text-color)] p-3 md:p-4 rounded-[12px] shadow-[0_10px_20px_var(--shadow-color)] border border-solid border-[var(--glass-border)] flex items-center gap-[10px]">
                                <div className="w-[10px] h-[10px] rounded-full bg-[#00cec9]"></div>
                                <span className="text-xs md:text-[0.8rem] font-bold">Task Completed</span>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <section className='py-20 md:py-32 bg-[var(--section-bg)] transition-colors duration-300 ease-in-out'>
                <div className='max-w-[1200px] mx-auto px-6 md:px-8'>
                    <div className="text-center mb-12 md:mb-16">
                        <h2 className="text-3xl md:text-[2.5rem] mb-4">Built for Scale</h2>
                        <p className="text-[var(--text-secondary)]">Everything you need to manage complex organizational structures.</p>
                    </div>
                    <div className='grid grid-cols-1 md:grid-cols-2 gap-6 md:gap-8'>
                        {features.map(feature => (
                            <Feature key={feature.title} icon={feature.icon} title={feature.title} description={feature.description} />
                        ))}
                    </div>
                </div>
            </section>

            <Footer />

        </div>
    )
}

export default Landing