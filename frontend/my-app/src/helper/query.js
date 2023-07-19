import axios from "axios";
const {REACT_APP_API_BASE_URL: BASE_URL} = process.env;

export const query = async ({urlExtension, accessToken})=> {
    return axios.get(BASE_URL+urlExtension,
        {
            headers: { 
                'Content-Type': 'application/json', 
                'Authorization': "Bearer "+ accessToken,
            }, 
        }
    );
}