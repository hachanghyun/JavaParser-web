import { useEffect, useState } from 'react';
import axios from 'axios';

function MainView({ project }) {
    const [structure, setStructure] = useState([]);
    const [messages, setMessages] = useState([]);
    const [question, setQuestion] = useState('');

    useEffect(() => {
        if (project?.projectId) {
            axios.get(`http://localhost:8080/api/source/all?projectId=${project.projectId}`)
                .then(res => setStructure(res.data))
                .catch(err => console.error('구조 조회 실패:', err));
            setMessages([]); // 새로운 프로젝트 선택 시 채팅 초기화
        }
    }, [project]);

    const handleSend = async () => {
        if (!question.trim()) return;

        const userMsg = { role: 'user', content: question };
        setMessages(prev => [...prev, userMsg]);

        try {
            const res = await axios.post('http://localhost:8000/ask',
                { question },
                {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            );

            const botMsg = { role: 'bot', content: res.data.answer };
            setMessages(prev => [...prev, botMsg]);
            setQuestion('');
        } catch (e) {
            console.error('❌ 챗봇 응답 실패:', e);
            const errorMsg = { role: 'bot', content: '❌ 챗봇 응답에 실패했습니다.' };
            setMessages(prev => [...prev, errorMsg]);
        }
    };

    if (!project) return <div style={{ padding: '24px', flex: 1 }}>프로젝트를 선택해주세요.</div>;

    return (
        <div style={{ padding: '24px', flex: 1 }}>
            <h2 style={{ fontSize: '20px', fontWeight: 'bold' }}>{project.name}</h2>

            <div style={{ marginBottom: '16px' }}>
                <h4>📦 구조 요약</h4>
                <ul>
                    {structure.map((cls, idx) => (
                        <li key={idx}><b>{cls.className}</b> - {cls.methods.length} 메서드</li>
                    ))}
                </ul>
            </div>

            <div style={{ border: '1px solid #ccc', padding: '16px', height: '300px', overflowY: 'auto', marginBottom: '16px' }}>
                <h4>🤖 챗봇</h4>
                {messages.map((msg, idx) => (
                    <div key={idx} style={{ margin: '8px 0' }}>
                        <b>{msg.role === 'user' ? '🙋‍♂️ 질문' : '🤖 응답'}:</b>
                        <pre style={{ whiteSpace: 'pre-wrap' }}>{msg.content}</pre>
                    </div>
                ))}
            </div>

            <div style={{ display: 'flex', gap: '8px' }}>
                <input
                    value={question}
                    onChange={e => setQuestion(e.target.value)}
                    placeholder="예: getUser 메서드는 어떤 역할이야?"
                    style={{ flex: 1, padding: '8px' }}
                />
                <button onClick={handleSend} style={{ padding: '8px 12px' }}>전송</button>
            </div>
        </div>
    );
}

export default MainView;
