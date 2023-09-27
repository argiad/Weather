//
//  URLProvider.swift
//  Weather
//
//  Created by Artem Mkrtchyan on 9/27/23.
//

import Foundation


//https://api.openweathermap.org/geo/1.0/direct?q=London&limit=5&appid={API key}
//https://api.openweathermap.org/data/2.5/weather?lat=33.44&lon=-9ee4.04&appid={API key}

class URLProvider {
    
    private static let baseURL = "https://api.openweathermap.org"
    private static let apiKey = "66a33599fa077ced1f765ab1d59e423b" // TODO: Move to config variables
    static let imageURL = "https://openweathermap.org/img/wn/%@@2x.png"
    
    enum Endpoint {
        case geo(_ str : String)
        case weather (_ geo : GeoCoding)
        
        var endpointPathString: String {
            switch self {
            case .geo(let text) :
                return  "\(URLProvider.baseURL)/geo/1.0/direct?q=\(text),US&limit=5&appid=\(URLProvider.apiKey)"
            case .weather(let geodata) :
                return  "\(URLProvider.baseURL)/data/2.5/weather?lat=\(geodata.lat!)&lon=\(geodata.lon!)&appid=\(URLProvider.apiKey)"

            }
        }
    }
    
}
