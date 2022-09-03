<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Film extends Model
{
    use HasFactory;
    protected $table = 'films';
    protected $fillable = ['title','story','duration','date_released','image'];
    protected $primaryKey = 'id';
    public $timestamps = FALSE;
}
