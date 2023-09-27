//
//  ContentView.swift
//  Weather
//
//  Created by Artem Mkrtchyan on 9/27/23.
//

import SwiftUI
import UIKit

let networkHelper = NetworkHelper()

struct ContentView : View {
    @State private var searchText = ""
    
    @State private var _cities: [GeoCoding] = []
    
    @State private var _selectedCity: GeoCoding? = nil
    
    var body: some View {
        NavigationStack {
            List {
                ForEach(_cities, id: \.self) { city in
                    NavigationLink(tag: city, selection: $_selectedCity) {
                        DetailView(city: city)
                    } label: {
                        Text("\(city.name!) , \(city.state! )")
                    }
                }
            }
            .navigationTitle("Search")
        }
        .searchable(text: $searchText) {
            ForEach(_cities, id: \.self) { city in
                Text("\(city.name!)").searchCompletion(city.name!)
            }
        }
        .onChange(of: searchText) { newQuery in
            if (newQuery.isEmpty) {
                _cities.removeAll()
            } else {
                runSearch(matching: newQuery)
            }
        }
        .onAppear(){
            restoreLast()
            if !searchText.isEmpty {
                runSearch(matching: searchText )
            }
        }
        
    }
    
    func runSearch(matching: String) {
        networkHelper.getGeoData(for: matching) { result in
            switch result {
            case .success(let cities):
                _cities = cities
            case .failure(let e):
                // TODO: need to add error behavior
                _cities.removeAll()
            }
        }
    }
    
    private let defaults = UserDefaults.standard

    
    private func restoreLast() {
        if let cityJson = defaults.string(forKey: "city"), let city = try? JSONDecoder().decode(GeoCoding.self, from: Data(cityJson.utf8)) {
            _cities.removeAll()
            _cities.append(city)
        }
    }
}

struct DetailView: View {
    
    var city: GeoCoding
    
    @State private var _weather: WeatherResponse? = nil
    
    var body: some View {
        NavigationStack {
            List {
                if $_weather.wrappedValue != nil {
                    let main = _weather!.main!
                    Section{
                        DetailLine(leftCeil: "Temperature", rightCeil: ": \(main.temp!)")
                        DetailLine(leftCeil: "Feels like", rightCeil: ": \(main.feelsLike!)")
                        DetailLine(leftCeil: "Humidity", rightCeil: ": \(main.humidity!)")
                        DetailLine(leftCeil: "Preasure", rightCeil: ": \(main.pressure!)")
                        
                    } header: {
                        Text ("main")
                    }.listRowSeparator(.hidden)
                    
                    let w = _weather!.weather!.first!
                    
                    Section{
                        DetailLine(leftCeil: "State", rightCeil: ": \(w.main!)")
                        DetailLine(leftCeil: "Description", rightCeil: ": \(w.description!)")
                    }header: {
                        HStack {
                            AsyncImage(url: URL(string: String(format:URLProvider.imageURL, w.icon! ))){ image in
                                image
                                    .resizable()
                                    .scaledToFill()
                            } placeholder: {
                                ProgressView()
                            }
                                .frame(width: 24, height: 24)
                            Text ("Weather")
                        }
                    }.listRowSeparator(.hidden)
                }
                
            }
            .navigationTitle("\(city.name!), \(city.state!)")
        }
        .onAppear{
            saveLastCity(city)
            requestWeather()
        }
    }
    
    private func requestWeather(){
        networkHelper.getWeather(for: city) { result in
            switch result {
            case .success(let weather):
                _weather = weather
            case .failure(let e):
                print(e.localizedDescription)
            }
            
        }
    }
    
    private let defaults = UserDefaults.standard
    private func saveLastCity(_ city: GeoCoding){
        if let encodedCity = try? JSONEncoder().encode(city){
            defaults.set( String(data: encodedCity, encoding: .utf8) , forKey: "city")
        }
    }
    
}

struct DetailLine: View {
    let leftCeil: String
    let rightCeil: String
    
    var body: some View {
        HStack{
            Text (leftCeil)
                .frame(maxWidth: .infinity, alignment: .leading)
            Text (rightCeil)
                .frame(maxWidth: .infinity, alignment: .leading)
        }
    }
}

#Preview {
    ContentView()
}


struct User: Identifiable {
    let id: UUID
    let name: String
}

struct UserModel{
    private(set) var users :[User] = []
    
    mutating func addUser(_ user: User){
        users.append(user)
    }
}

class UserViewModel: ObservableObject{
    @Published private var userModel =  UserModel()
    
    var users: [User] {
        return userModel.users
    }
    
    func addUser(_ user: User){
        userModel.addUser(user)
    }
}

