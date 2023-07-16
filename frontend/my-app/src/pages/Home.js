import { useNavigate } from "react-router-dom";
import { useContext } from "react";
import AuthContext from "../context/AuthProvider";

const Home = () => {
    const { setAuth } = useContext(AuthContext);
    const navigate = useNavigate();

    const logout = async () => {
        //api call for logout 
        // const response = await axios.post(BASE_URL+REGISTER_URL,
        //     JSON.stringify({ username, email, password }),
        //     {
        //         headers: { 'Content-Type': 'application/json' },
        //     }
        // );
        setAuth({});
        navigate('/login');
    }

    return (
        <section>
            <h1>Home</h1>
            <div className="flexGrow">
                <button onClick={logout}>Sign Out</button>
            </div>
        </section>
    )
}

export default Home
