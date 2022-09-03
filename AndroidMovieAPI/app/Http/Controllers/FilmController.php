<?php


namespace App\Http\Controllers;

use Illuminate\Http\Request;

use Illuminate\Support\Facades\Response;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Facades\Auth;
use App\Models\User;
use App\Models\Film;
use App\Models\Actor;
use App\Models\Producer;
use App\Models\Genre;
use App\Models\Film_actor;
use App\Models\Film_producer;
use App\Models\Film_genre;
use DB;
use Validator;


class FilmController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        $data = Film::all();
        return json_encode(['data'=>$data]);
    }

    /**
     * Show the form for creating a new resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function create()
    {
        //
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {

        $Film  = new Film;
        $Film->title = $request->title;
        $Film->story = $request->story;
        $Film->duration = $request->duration;
        $Film->date_released = $request->date;
        $Film->image = 'storage/images/movies/'.$request->title.'.jpg';
        $Film->save();
        
        Storage::put('public/images/movies/'.$request->title.'.jpg',base64_decode($request->uploads));
        
        $newFilm_id = $Film->id;  //GETTING ID OF THE LAST INSERTED

        // //SAVING ACTORS TO A FILM
        $actor_ids = $request->actor_ids; //Gettinng string of ids
        $actor_ids_array = explode(',', $actor_ids); //splitting string into sepratae string using the comma
        foreach ($actor_ids_array as $key) {
            if($key != ""){
                $Film_actor = new Film_actor;
                $Film_actor->film_id = $newFilm_id;
                $Film_actor->actor_id = $key;
                $Film_actor->save();
            }
        }

        //SAVING PRODUCER TO A FILM
        $producer = $request->producer;
        //$producer = "ragg";
        $producer_id = DB::table('producers')->where('name', $producer)->get("id");
        foreach ($producer_id as $key) { 
            $Film_producer = new Film_producer;
            $Film_producer->film_id = $newFilm_id;
            $Film_producer->producer_id = $key->id;
            $Film_producer->save();
        }

        //SAVING GENRE TO A FILM
        $genre = $request->genre;
        //$genre = "horror";
        $genre_id = DB::table('genres')->where('genre', $genre)->get("id");
        foreach ($genre_id as $key) {
            $Film_genre = new Film_genre;
            $Film_genre->film_id = $newFilm_id;
            $Film_genre->genre_id = $key->id;
            $Film_genre->save();

        }

        $data=array('status' => 'saved');
        return Response::json($data,200);
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show($id)
    {
        $data_movie = Film::find($id);

        //getting producer of movie
        $data_producer = DB::table('film_producer')->where('film_id', $id)->get('producer_id');
        foreach ($data_producer as $key) {
            $producer = Producer::find($key->producer_id);
        }

        //getting genre of movie
        $data_genre = DB::table('film_genre')->where('film_id', $id)->get("genre_id");
        foreach ($data_genre as $key) {
            $genre = Genre::find($key->genre_id);
        } 

        //getting actors id using movie's id
        $data_actors = DB::table('film_actor')->where('film_id', $id)->get("actor_id"); 
        
        if(count($data_actors)>0){
            foreach ($data_actors as $key) {
                $actors[] = Actor::find($key->actor_id);

            }
            return json_encode(['data_movie'=>$data_movie, 'data_actors'=>$actors, 'producer' => $producer, "genre"=>$genre]);

        }else{
            return json_encode(['data_movie'=>$data_movie,'producer' => $producer, "genre"=>$genre]);
        }
       



       
    }

    /**
     * Show the form for editing the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function edit($id)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request, $id)
    {
        $actor_ids = $request->actor_ids;
        if($actor_ids != ""){
            //delete actor firs
            DB::table('film_actor')->where('film_id', $id)->delete();

            //add new set of actors
            $actor_ids_array = explode(',', $actor_ids); //splitting string into sepratae string using the comma
            foreach ($actor_ids_array as $key) {
                if($key != ""){
                    $Film_actor = new Film_actor;
                    $Film_actor->film_id = $id;
                    $Film_actor->actor_id = $key;
                    $Film_actor->save();
                }
            }

        }

        if($request->uploads == ""){
            $Film = Film::findOrFail($id);
            $Film->update($request->all());
            $data=array('status' => 'saved'); 
        }else{
            $Film = Film::findOrFail($id);
            $img_path = $request->title.'.jpg';
            $request['image'] = 'storage/images/movies/'.$img_path;
            $Film->update($request->all());
            Storage::put('public/images/movies/'.$img_path,base64_decode($request->uploads));
            $data=array('status' => 'saved'); 
        }

        return Response::json($data,200);
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy($id)
    {
        DB::table('film_actor')->where('film_id', $id)->delete();
        DB::table('film_genre')->where('film_id', $id)->delete();
        DB::table('film_producer')->where('film_id', $id)->delete();

        $Film = Film::findOrFail($id);
        $Film->delete();

        return json_encode(['status'=>'movie deleted']);


    }


}
