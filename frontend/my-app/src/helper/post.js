import axios from "axios";
const {REACT_APP_API_BASE_URL: BASE_URL} = process.env;

export const post = async ({urlExtension, body, accessToken})=> {
    return axios.post(BASE_URL+urlExtension,
        body,
        {
            headers: { 
                'Content-Type': 'application/json', 
                'Authorization': "Bearer "+ accessToken,
        }, 
        withCredentials:true
        } 
    );
}